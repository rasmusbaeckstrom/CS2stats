// OBS! Denna klass är endast för att testa att extrahera spelardata från en loggfil.

package org.rb.cs2stats.service;

import java.util.regex.*;

public class LogParser {

    private static final Pattern JSON_BEGIN_PATTERN = Pattern.compile("JSON_BEGIN\\{");
    private static final Pattern JSON_END_PATTERN = Pattern.compile("JSON_END");
    private static final Pattern PLAYERS_SECTION_PATTERN = Pattern.compile("\"players\"\\s*:\\s*\\{(.*?)\\}\\s*\\}\\s*JSON_END", Pattern.DOTALL);
    private static final Pattern PLAYER_LINE_PATTERN = Pattern.compile(
            "\"player_\\d+\"\\s*:\\s*\"\\s*(\\d+)\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*(\\d+)\\s*,\\s*(\\d+),.*?\""
    );

    public static void main(String[] args) {
        String logData = "L 07/03/2024 - 23:50:19: JSON_BEGIN{\n" +
                "L 07/03/2024 - 23:50:19: \"name\": \"round_stats\",\n" +
                "L 07/03/2024 - 23:50:19: \"round_number\" : \"19\",\n" +
                "L 07/03/2024 - 23:50:19: \"score_t\" : \"12\",\n" +
                "L 07/03/2024 - 23:50:19: \"score_ct\" : \"6\",\n" +
                "L 07/03/2024 - 23:50:19: \"map\" : \"de_cache\",\n" +
                "L 07/03/2024 - 23:50:19: \"server\" : \"Sommarlan 2024 Server 2\",\n" +
                "L 07/03/2024 - 23:50:19: \"fields\" : \"             accountid,   team,  money,  kills, deaths,assists,    dmg,    hsp,    kdr,    adr,    mvp,     ef,     ud,     3k,     4k,     5k,clutchk, firstk,pistolk,sniperk, blindk,  bombk,firedmg,uniquek,  dinks,chickenk\"\n" +
                "L 07/03/2024 - 23:50:19: \"players\" : {\n" +
                "L 07/03/2024 - 23:50:19: \"player_0\" : \"              448926,      3,   5250,      8,     15,      2,   1169,  75.00,   0.53,     65,      1,      7,      8,      0,      0,      0,      1,      3,      4,      1,      0,      1,      4,      4,      6,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_1\" : \"              610675,      2,   8550,     19,      9,      4,   1953,  42.11,   2.11,    108,      4,      5,     67,      1,      0,      0,      0,      2,      3,      0,      0,      1,     56,     11,      6,      1\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_2\" : \"            25952377,      3,   5700,     13,     14,      5,   1490,  30.77,   0.93,     83,      2,     12,     95,      1,      0,      0,      3,      1,      3,      0,      1,      2,     44,     51,      5,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_3\" : \"             1160067,      2,   8750,     11,      9,      8,   1501,  18.18,   1.22,     83,      0,     10,    138,      0,      0,      0,      1,      0,      3,      0,      1,      2,      0,    138,      5,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_4\" : \"           123756645,      2,   5150,     17,     12,      3,   1638,  23.53,   1.42,     91,      4,      1,    169,      1,      0,      0,      0,      4,      5,      0,      2,      4,     11,    161,      6,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_5\" : \"           121638685,      3,   5300,     10,     16,      4,   1177,  40.00,   0.62,     65,      1,      3,     49,      1,      0,      0,      1,      2,      1,      0,      0,      0,     44,      5,      4,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_6\" : \"            24037629,      3,   5550,      9,     16,      2,    894,  22.22,   0.56,     50,      1,      6,      2,      0,      0,      0,      2,      0,      4,      0,      2,      1,      2,      0,      6,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_7\" : \"           369890250,      3,   5150,     12,     15,      3,   1540,  66.67,   0.80,     86,      1,      2,    124,      1,      0,      0,      1,      1,      6,      0,      0,      1,    115,      9,      5,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_8\" : \"            75774945,      2,   9700,     20,     10,      4,   1830,  50.00,   2.00,    102,      3,      3,    113,      2,      1,      0,      2,      3,      2,      5,      0,      6,      8,    105,      6,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_9\" : \"              256187,      2,   5450,      9,     13,      5,   1170,  55.56,   0.69,     65,      1,      1,     62,      0,      0,      0,      0,      2,      1,      0,      0,      1,     37,     26,      4,      0\"\n" +
                "L 07/03/2024 - 23:50:19: \"player_10\" : \"                   0,      0,    800,      0,      0,      0,      0,   0.00,   0.00,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      0\"\n" +
                "L 07/03/2024 - 23:50:19: }}JSON_END";

                parseLog(logData);
    }

    public static void parseLog(String logData) {
        Matcher jsonBeginMatcher = JSON_BEGIN_PATTERN.matcher(logData);
        Matcher jsonEndMatcher = JSON_END_PATTERN.matcher(logData);

        int lastJsonStart = -1;
        int lastJsonEnd = -1;

        // Hitta den sista JSON-sektionen innan "Game Over".
        while (jsonBeginMatcher.find()) {
            lastJsonStart = jsonBeginMatcher.start();
        }
        while (jsonEndMatcher.find()) {
            lastJsonEnd = jsonEndMatcher.end();
        }

        if (lastJsonStart != -1 && lastJsonEnd != -1 && lastJsonStart < lastJsonEnd) {
            String jsonSegment = logData.substring(lastJsonStart, lastJsonEnd);

            // Extrahera "players" sektionen
            Matcher playersSectionMatcher = PLAYERS_SECTION_PATTERN.matcher(jsonSegment);
            if (playersSectionMatcher.find()) {
                String playersData = playersSectionMatcher.group(1);

                // Extrahera varje spelares data
                Matcher playerLineMatcher = PLAYER_LINE_PATTERN.matcher(playersData);
                while (playerLineMatcher.find()) {
                    String accountId = playerLineMatcher.group(1);
                    int kills = Integer.parseInt(playerLineMatcher.group(2));
                    int deaths = Integer.parseInt(playerLineMatcher.group(3));

                    System.out.printf("Player %s: Kills = %d, Deaths = %d%n", accountId, kills, deaths);
                }
            } else {
                System.out.println("No players section found in the JSON segment.");
            }
        } else {
            System.out.println("No valid JSON segment found.");
        }
    }
}
