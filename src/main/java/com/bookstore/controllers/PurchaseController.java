package com.bookstore.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookstore.beans.Purchase;
import com.bookstore.dao.PurchaseDao;
import com.bookstore.metrics.CustomMetrics;

@Controller
@RequestMapping("purchase")
public class PurchaseController {

    @Autowired
    PurchaseDao dao;

    @RequestMapping("/purchaseform")
    public String showform(Model m) {
        m.addAttribute("command", new Purchase());
        CustomMetrics.incrementTotalRequests();
        return "purchaseform";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("purchase") Purchase purchase) {
        dao.save(purchase);
        CustomMetrics.incrementTotalRequests();
        return "redirect:/purchase/viewpurchases";
    }

    @RequestMapping("/viewpurchases")
    public String viewbooks(Model m) {
        List<Purchase> list = dao.getPurchases();
        m.addAttribute("list", list);
        CustomMetrics.incrementTotalRequests();
        return "viewpurchases";
    }

    @RequestMapping(value = "/editpurchase/{id}")
    public String edit(@PathVariable int id, Model m) {
        Purchase purchase = dao.getPurchaseById(id);
        m.addAttribute("command", purchase);
        CustomMetrics.incrementTotalRequests();
        return "purchaseeditform";
    }

    @RequestMapping(value = "/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("purchase") Purchase purchase) {
        dao.update(purchase);
        CustomMetrics.incrementTotalRequests();
        return "redirect:/purchase/viewpurchases";
    }

    @RequestMapping(value = "/deletepurchase/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable int id) {
        dao.delete(id);
        CustomMetrics.incrementTotalRequests();
        return "redirect:/purchase/viewpurchases";
    }
}
