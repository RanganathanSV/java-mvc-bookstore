package com.bookstore.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookstore.beans.Book;
import com.bookstore.dao.BookDao;
import com.bookstore.metrics.CustomMetrics;

@Controller
@RequestMapping("book")
public class BookController {

    @Autowired
    BookDao dao;

    @RequestMapping("/bookform")
    public String showform(Model m) {
        m.addAttribute("command", new Book());
        CustomMetrics.incrementTotalRequests();
        return "bookform";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("book") Book book) {
        dao.save(book);
        CustomMetrics.incrementTotalRequests();
        return "redirect:/book/viewbooks";
    }

    @RequestMapping("/viewbooks")
    public String viewbooks(Model m) {
        List<Book> list = dao.getBooks();
        m.addAttribute("list", list);
        CustomMetrics.incrementTotalRequests();
        return "viewbooks";
    }

    @RequestMapping(value = "/editbook/{id}")
    public String edit(@PathVariable int id, Model m) {
        Book book = dao.getBookById(id);
        m.addAttribute("command", book);
        CustomMetrics.incrementTotalRequests();
        return "bookeditform";
    }

    @RequestMapping(value = "/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("book") Book book) {
        dao.update(book);
        CustomMetrics.incrementTotalRequests();
        return "redirect:/book/viewbooks";
    }

    @RequestMapping(value = "/deletebook/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable int id) {
        dao.delete(id);
        CustomMetrics.incrementTotalRequests();
        return "redirect:/book/viewbooks";
    }
}
