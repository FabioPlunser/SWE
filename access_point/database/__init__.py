"""
Handles the persistent local data storage in a SQLite database.

Classes:
    - Database
    - DatabaseError

Miscellaneous variables:
    - DB_FILENAME
"""

from .database import Database, DatabaseError, DB_FILENAME