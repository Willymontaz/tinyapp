package com.xebia.tinyapp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcPool extends HttpServlet {

    private static InitialContext initialContext;
    private static DataSource dataSource;

    @Override
    public void init() throws ServletException {
        if(dataSource == null){
            try {
                dataSource = getDataSource();
            } catch (Exception e) {
                throw new ServletException("Cannot create pooled connection", e);
            }
        }
        super.init();
    }

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            long start = System.nanoTime();
            resultSet = statement.executeQuery("SELECT * FROM employees");
            long end = System.nanoTime();

            resp.getWriter().println("Took : "+(end - start)/1000000+ "ms");
            while (resultSet.next()) {
                resp.getWriter().println("---");
                resp.getWriter().println(resultSet.getInt("id"));
                resp.getWriter().println(resultSet.getString("first_name"));
                resp.getWriter().println(resultSet.getString("last_name"));
                resp.getWriter().println(resultSet.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
		
		
	}

    private DataSource getDataSource() throws NamingException, SQLException {
        Context initCtx = new InitialContext();
        DataSource dataSource = (DataSource) initCtx.lookup("java:/comp/env/jdbc/hsqldb");
        return dataSource;
    }

}
