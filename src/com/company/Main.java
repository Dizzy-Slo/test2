package com.company;

import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws IOException {
        URL url = new URL("https://onlinesim.ru/price-list-data?type=receive");
        Parser parser = new Parser(url);
        parser.ParseJSON();
        System.out.println(parser.getResultJSON());
    }
}
