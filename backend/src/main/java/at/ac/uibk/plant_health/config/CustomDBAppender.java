package at.ac.uibk.plant_health.config;

import static java.util.Map.entry;

import org.springframework.data.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.db.names.ColumnName;
import ch.qos.logback.classic.db.names.DBNameResolver;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.classic.db.names.TableName;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBHelper;
import ch.qos.logback.core.db.dialect.SQLDialectCode;

// https://stackoverflow.com/a/22507502
public class CustomDBAppender extends DBAppender {
	protected DBNameResolver dbNameResolver;

	private String INSERT_SQL;

	private static long id = 0;

	private final List<String> TABLE_NAMES;

	// https://logback.qos.ch/manual/appenders.html#DBAppender
	// TODO: Add Primary Key to EventId in every Table
	private static final Map<TableName, List<Pair<ColumnName, JDBCType>>> TABLES = Map.ofEntries(
			entry(TableName.LOGGING_EVENT,
				  List.of(Pair.of(ColumnName.TIMESTMP, JDBCType.BIGINT),
						  Pair.of(ColumnName.FORMATTED_MESSAGE, JDBCType.VARCHAR),
						  Pair.of(ColumnName.LOGGER_NAME, JDBCType.VARCHAR),
						  Pair.of(ColumnName.LEVEL_STRING, JDBCType.VARCHAR),
						  Pair.of(ColumnName.THREAD_NAME, JDBCType.VARCHAR),
						  Pair.of(ColumnName.REFERENCE_FLAG, JDBCType.SMALLINT),
						  Pair.of(ColumnName.ARG0, JDBCType.VARCHAR),
						  Pair.of(ColumnName.ARG1, JDBCType.VARCHAR),
						  Pair.of(ColumnName.ARG2, JDBCType.VARCHAR),
						  Pair.of(ColumnName.ARG3, JDBCType.VARCHAR),
						  Pair.of(ColumnName.CALLER_FILENAME, JDBCType.VARCHAR),
						  Pair.of(ColumnName.CALLER_CLASS, JDBCType.VARCHAR),
						  Pair.of(ColumnName.CALLER_METHOD, JDBCType.VARCHAR),
						  Pair.of(ColumnName.CALLER_LINE, JDBCType.INTEGER),
						  Pair.of(ColumnName.EVENT_ID, JDBCType.INTEGER))),
			entry(TableName.LOGGING_EVENT_PROPERTY,
				  List.of(Pair.of(ColumnName.EVENT_ID, JDBCType.INTEGER),
						  Pair.of(ColumnName.MAPPED_KEY, JDBCType.VARCHAR),
						  Pair.of(ColumnName.MAPPED_VALUE, JDBCType.VARCHAR))),
			entry(TableName.LOGGING_EVENT_EXCEPTION,
				  List.of(Pair.of(ColumnName.EVENT_ID, JDBCType.INTEGER),
						  Pair.of(ColumnName.I, JDBCType.SMALLINT),
						  Pair.of(ColumnName.TRACE_LINE, JDBCType.VARCHAR)))
	);

	public CustomDBAppender() {
		super();
		// Ensure that the DBAppender uses the same DB-Name Resolver.
		this.dbNameResolver = new DefaultDBNameResolver();
		super.setDbNameResolver(this.dbNameResolver);
		this.TABLE_NAMES =
				TABLES.keySet().stream().map(t -> this.dbNameResolver.getTableName(t)).toList();

		this.INSERT_SQL = buildInsertSQL();
	}

	private String buildInsertSQL() {
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");

		sqlBuilder.append(dbNameResolver.getTableName(TableName.LOGGING_EVENT)).append(" (");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.TIMESTMP)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.FORMATTED_MESSAGE)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LOGGER_NAME)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LEVEL_STRING)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.THREAD_NAME)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.REFERENCE_FLAG)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG0)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG1)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG2)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG3)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_FILENAME)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_CLASS)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_METHOD)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_LINE)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID)).append(") ");
		sqlBuilder.append("VALUES (?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		return sqlBuilder.toString();
	}

	private List<String> checkTablesExist(List<String> tableNames) {
		Connection connection;
		try {
			connection = connectionSource.getConnection();
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = "
			);
			queryBuilder.append('\'');
			queryBuilder.append(connection.getCatalog());
			queryBuilder.append('\'');
			queryBuilder.append(" AND table_name IN (");

			// Define Prefix Variable to avoid inserting ',' before first Field
			for (String tableName : tableNames) {
				queryBuilder.append('\'');
				queryBuilder.append(tableName);
				queryBuilder.append('\'');
				queryBuilder.append(',');
			}
			// Remove trailing ','
			queryBuilder.deleteCharAt(queryBuilder.length() - 1);
			queryBuilder.append(")");

			try {
				Statement stmt = connection.createStatement();
				stmt.execute(queryBuilder.toString());
				List<String> existingTables = new ArrayList<>();
				ResultSet set = stmt.getResultSet();
				while (set.next()) existingTables.add(set.getString(1));
				return existingTables;
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
		return List.of();
	}

	@Override
	public void start() {
		Connection connection;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);

			List<String> existingTables = this.checkTablesExist(this.TABLE_NAMES);

			for (Map.Entry<TableName, List<Pair<ColumnName, JDBCType>>> table : TABLES.entrySet()) {
				TableName tableName = table.getKey();
				List<Pair<ColumnName, JDBCType>> fields = table.getValue();

				if (existingTables.contains(this.dbNameResolver.getTableName(tableName))) {
					alterTable(tableName, fields);
				} else {
					createTable(tableName, fields);
				}
			}
			connection.commit();
		} catch (Exception e) {
		}

		super.start();
	}

	public boolean createTable(TableName tableName, List<Pair<ColumnName, JDBCType>> fields) {
		StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
		queryBuilder.append(this.dbNameResolver.getTableName(tableName));
		queryBuilder.append(" (");

		// Define Prefix Variable to avoid inserting ',' before first Field
		for (Pair<ColumnName, JDBCType> field : fields) {
			queryBuilder.append(this.dbNameResolver.getColumnName(field.getFirst()));
			queryBuilder.append(' ');
			queryBuilder.append(field.getSecond().getName());

			// TODO: Create Class for SQL Column?
			if (field.getSecond().getName().equals(JDBCType.VARCHAR.getName())) {
				queryBuilder.append("(1000)");
			} else if (field.getSecond().getName().equals(JDBCType.INTEGER.getName())) {
				queryBuilder.append(" DEFAULT 0");
			}
			// queryBuilder.append(" NOT NULL");
			queryBuilder.append(',');
		}
		queryBuilder.append("PRIMARY KEY (");
		queryBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID));
		queryBuilder.append(")");

		return executeStatement(null, queryBuilder.toString());
	}

	public boolean alterTable(TableName tableName, List<Pair<ColumnName, JDBCType>> fields) {
		StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
		queryBuilder.append(this.dbNameResolver.getTableName(tableName));
		queryBuilder.append(' ');

		for (Pair<ColumnName, JDBCType> field : fields) {
			queryBuilder.append("MODIFY COLUMN ");
			queryBuilder.append(this.dbNameResolver.getColumnName(field.getFirst()));
			queryBuilder.append(' ');
			queryBuilder.append(field.getSecond().getName());
			// TODO: Create Class for SQL Column?
			if (field.getSecond().getName().equals(JDBCType.VARCHAR.getName())) {
				queryBuilder.append("(1000)");
			} else if (field.getSecond().getName().equals(JDBCType.INTEGER.getName())) {
				queryBuilder.append(" DEFAULT 0");
			}
			// queryBuilder.append(" NOT NULL");
			queryBuilder.append(',');
		}
		queryBuilder.append("PRIMARY KEY (");
		queryBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID));
		queryBuilder.append(";");

		return executeStatement(null, queryBuilder.toString());
	}

	public boolean executeStatement(Connection connection, String query) {
		try {
			if (connection == null) connection = connectionSource.getConnection();
			Statement stmt = connection.createStatement();
			stmt.execute(query);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void append(ILoggingEvent eventObject) {
		System.out.println("Append called");
		Connection connection = null;
		PreparedStatement insertStatement = null;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);

			if (cnxSupportsGetGeneratedKeys) {
				String EVENT_ID_COL_NAME = "EVENT_ID";
				// see
				if (connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
					EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
				}
				insertStatement = connection.prepareStatement(
						getInsertSQL(), new String[] {EVENT_ID_COL_NAME}
				);
			} else {
				insertStatement = connection.prepareStatement(getInsertSQL());
			}

			long eventId;
			// inserting an event and getting the result must be exclusive
			synchronized (this) {
				// TODO: Get Select ID from DBAppender to work.
				eventId = id;
				id++;
				insertStatement.setLong(15, eventId);
				System.out.println(insertStatement);
				subAppend(eventObject, connection, insertStatement);
				System.out.println(insertStatement);
			}
			secondarySubAppend(eventObject, connection, eventId);

			System.out.println("Commiting");

			connection.commit();
		} catch (Throwable sqle) {
			System.out.println(sqle);
		} finally {
			DBHelper.closeStatement(insertStatement);
			DBHelper.closeConnection(connection);
		}
	}

	@Override
	public String getInsertSQL() {
		return this.INSERT_SQL;
	}
}