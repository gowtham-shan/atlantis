package com.halnode.atlantis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String args[]) {
        String username = "9090909090";
        Pattern P = Pattern.compile("(6-9)?[6-9][0-9]{9}");
        Matcher m = P.matcher(username);
        System.out.println((m.find() && m.group().equals(username)));

    }
}
