/*
  Enter custom T-SQL here that would run after PostgresSQL has started up.
  We shouldn't have to do too much here since the ORM will manage the creation of tables, etc.
*/

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'warehouse_db')
BEGIN
  CREATE DATABASE warehouse_db;
END;
GO

