package com.trainDelay.calculator.phase2;

import com.trainDelay.calculator.Ticket;

import java.util.Arrays;
import java.util.List;

public class TicketParser {

    private static final List<String> TICKET_TYPES = Arrays.asList(
        "off-peak day single", "off-peak day return", "peak day single", "peak day return"
    );

    public static Ticket parseTicket(String text) {
        Ticket ticket = new Ticket();
        String[] lines = text.split("\\r?\\n");

        // Extract and format ticket type
        String ticketType = lines[0].toLowerCase();
        ticket.setTicketType(formatTicketType(ticketType));

        // Extract toStation and fromStation
        for (String line : lines) {
            if (line.toLowerCase().startsWith("to")) {
                ticket.setToStation(line.substring(2).trim());
            } else if (line.toLowerCase().startsWith("from")) {
                ticket.setFromStation(line.substring(4).trim());
            }
        }

        return ticket;
    }

    private static String formatTicketType(String ticketType) {
        return TICKET_TYPES.stream()
            .min((type1, type2) -> Integer.compare(
                levenshteinDistance(ticketType, type1),
                levenshteinDistance(ticketType, type2)
            ))
            .orElse(ticketType);
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }

        return dp[a.length()][b.length()];
    }
}
