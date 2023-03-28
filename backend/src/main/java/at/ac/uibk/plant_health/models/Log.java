package at.ac.uibk.plant_health.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "log_id", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID logId;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "severity", nullable = false)
    private LogLevel severity;

    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "message", nullable = false)
    private String message;

    public enum LogLevel {
        FATAL(32),
        ERROR(16),
        WARN(8),
        INFO(4),
        DEBUG(2),
        TRACE(1),
        ALL(63),
        OFF(0);

        @Getter
        private final int level;
        LogLevel(int value) {
            this.level = value;
        }
    }

    @Converter(autoApply = true)
    public static class LogLevelConverter implements AttributeConverter<LogLevel, String> {

        @Override
        public String convertToDatabaseColumn(LogLevel logLevel) {
            if (logLevel == null) {
                return null;
            }
            return logLevel.toString();
        }

        @Override
        public LogLevel convertToEntityAttribute(String logLevel) {
            if (logLevel == null) {
                return null;
            }

            return LogLevel.valueOf(logLevel);
        }
    }
}
