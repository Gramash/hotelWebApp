package Servlets;

import JavaBeans.Product;
import JavaBeans.UserAccount;
import MySQL.OrdersTable;
import MySQL.ProductTable;
import Utils.AppUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/productView")
public class ProductViewServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Product> productList;
        productList = ProductTable.extractAll();
        request.setAttribute("productList", productList);

        RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/Views/ProductView.jsp");
        rq.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = AppUtils.getLoginedUser(req.getSession());

        if (user == null) {
            RequestDispatcher dispatcher //
                    = req.getRequestDispatcher("/WEB-INF/Views/loginView.jsp");

            dispatcher.forward(req, resp);
        }

        int userId = AppUtils.getLoginedUser(req.getSession()).getUserID();
        int productId = Integer.parseInt(req.getParameter("id"));
        String checkIn = req.getParameter("checkIn");
        String checkOut = req.getParameter("checkOut");

        if (ProductTable.isTakenById(productId, checkIn, checkOut)) {
            System.out.println("isTaken");
            req.setAttribute("message", "taken for this dates");
            doGet(req, resp);
        }
        if (OrdersTable.insertOrder(userId, productId, checkIn, checkOut)) {
            req.setAttribute("message", "You have successfully made an order.");
            doGet(req, resp);
        }


    }
}
