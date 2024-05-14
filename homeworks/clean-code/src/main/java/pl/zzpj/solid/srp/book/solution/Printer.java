package pl.zzpj.solid.srp.book.solution;

import java.util.Map;

public class Printer implements Printable {

    @Override
    public void printCurrentPage(Book book) {
        System.out.println(book.getCurrentPageContents());
    }

    @Override
    public String printAllPages(Book book) {
        StringBuilder allPages = new StringBuilder();
        for(Map.Entry<Integer, String> page : book.getPages().entrySet()) {
            allPages.append(page.getKey()).append(" ").append(page.getValue());
        }
        return allPages.toString();
    }
}
