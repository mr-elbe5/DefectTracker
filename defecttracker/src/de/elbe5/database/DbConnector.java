/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.database;

import de.elbe5.base.log.Log;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbConnector {

    private static DbConnector instance = null;

    public static DbConnector getInstance() {
        if (instance == null) {
            instance = new DbConnector();
        }
        return instance;
    }

    private DataSource dataSource = null;

    public boolean initialize(String name) {
        if (dataSource != null) {
            return true;
        }
        try {
            Log.log("initializing database...");
            InitialContext initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup(name);
            Log.log("trying to connect...");
            Connection con = null;
            try {
                con = dataSource.getConnection();
                if (con == null || con.isClosed()) {
                    dataSource = null;
                }
            } catch (Exception e) {
                dataSource = null;
            }
            if (con != null) {
                Log.log("connection ok");
                try {
                    con.close();
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            Log.error("error during database initialization", e);
            dataSource = null;
        }
        if (dataSource != null) {
            Log.log("data source successfully created");
        } else {
            Log.error("cannot create valid connection");
        }
        return dataSource != null;
    }

    public Connection getConnection() {
        try {
            if (dataSource == null) {
                return null;
            }
            return dataSource.getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    public Connection startTransaction() {
        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            return con;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean commitTransaction(Connection con) {
        boolean result = true;
        try {
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception e) {
            result = false;
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public boolean rollbackTransaction(Connection con) {
        return rollbackTransaction(con, null);
    }

    public boolean rollbackTransaction(Connection con, Exception e) {
        try {
            if (e != null)
                Log.error("rollback", e);
            con.rollback();
            con.setAutoCommit(true);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return false;
    }

    public void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception ignore) {/* do nothing */

        }
    }

    public void closeStatement(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (Exception ignore) {/* do nothing */

        }
    }

    public boolean executeScript(String sql) {
        Connection con = null;
        try {
            con = startTransaction();
            if (!executeScript(con, sql)) {
                rollbackTransaction(con);
                return false;
            }
            commitTransaction(con);
            return true;
        } finally {
            closeConnection(con);
        }
    }

    public static List<String> splitScript(String src) {
        List<String> list = new ArrayList<>();
        LineNumberReader reader = new LineNumberReader(new StringReader(src));
        StringBuilder sb = null;
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if (line.startsWith("--")) {
                    if (sb != null) {
                        String command = sb.toString().trim();
                        if (!command.isEmpty()) {
                            list.add(command);
                        }
                        sb = null;
                    }
                    continue;
                }
                if (sb == null)
                    sb = new StringBuilder();
                sb.append(line).append('\n');
            }
            if (sb != null) {
                String command = sb.toString().trim();
                if (!command.isEmpty()) {
                    list.add(command);
                }
            }
        } catch (IOException ex) {
            Log.error("unable to parse sql", ex);
            list.clear();
        }
        return list;
    }

    public boolean executeScript(Connection con, String sql) {
        int count = 0;
        List<String> commands = splitScript(sql);
        for (String command : commands) {
            if (!executeCommand(con, command.trim())) {
                return false;
            }
            count++;
        }
        Log.info("executed " + count + " statement(s)");
        return true;
    }

    protected String adjustCommand(String sqlCmd) {
        return sqlCmd.trim();
    }

    public boolean executeCommand(Connection con, String sqlCmd) {
        String cmd = adjustCommand(sqlCmd);
        Statement stmt;
        try {
            if (sqlCmd.length() == 0) {
                return true;
            }
            stmt = con.createStatement();
            if (cmd.toLowerCase().startsWith("select"))
                stmt.executeQuery(cmd);
            else
                stmt.executeUpdate(cmd);
            stmt.close();
            return true;
        } catch (SQLException e) {
            Log.error("error on sql command", e);
            return false;
        }
    }

    public boolean checkSelect(String sql) {
        Connection con = null;
        boolean result = false;
        try {
            con = getConnection();
            if (con != null) {
                result = checkSelect(con, sql);
            }
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public boolean checkSelect(Connection con, String sqlCmd) {
        String cmd = sqlCmd.replace(';', ' ').trim();
        cmd = adjustCommand(cmd);
        Statement stmt;
        boolean result;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(cmd);
            result = rs.next();
            stmt.close();
            return result;
        } catch (SQLException e) {
            return false;
        }
    }
}
