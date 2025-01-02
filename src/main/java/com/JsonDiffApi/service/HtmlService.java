package com.JsonDiffApi.service;

import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Map;

@Service
public class HtmlService {

    public String generateHtml(String originalJson, String incomingJson, List<Map<String, Object>> differences) {
        // Beautify JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String beautifiedOriginalJson = gson.toJson(gson.fromJson(originalJson, Object.class));
        String beautifiedIncomingJson = gson.toJson(gson.fromJson(incomingJson, Object.class));

        StringBuilder html = new StringBuilder();

        html.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 40px; }")
                .append("h2 { color: #333; }")
                .append(".container { display: flex; justify-content: space-between; }")
                .append(".json-box { width: 48%; padding: 10px; border: 1px solid #ddd; background-color: #f9f9f9; }")
                .append(".json-box pre { white-space: pre-wrap; word-wrap: break-word; }")
                .append(".info { background-color: #e8e8e8; padding: 10px; border-radius: 5px; margin-bottom: 20px; }")
                .append(".replace { background-color: #ffeb3b; }")
                .append(".remove { background-color: #f44336; }")
                .append(".add { background-color: #4caf50; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<h2>JSON Comparison</h2>")
                .append("<div class='info'>")
                .append("<strong>Legend:</strong> ")
                .append("<span style='background-color: #ffeb3b; padding: 3px;'>Replace</span> ")
                .append("<span style='background-color: #f44336; padding: 3px;'>Remove</span> ")
                .append("<span style='background-color: #4caf50; padding: 3px;'>Add</span> ")
                .append("</div>")

                // Show the differences right after the legend, before JSON sections
                .append("<h3>Differences</h3>")
                .append("<table border='1'><tr><th>Field</th><th>Operation</th><th>Old Value</th><th>New Value</th></tr>");

        // Add differences to the HTML table
        for (Map<String, Object> diff : differences) {
            String operation = (String) diff.get("operation");
            String field = (String) diff.get("field");
            String oldValue = diff.get("oldValue") == null ? "N/A" : diff.get("oldValue").toString();
            String newValue = diff.get("newValue") == null ? "N/A" : diff.get("newValue").toString();

            html.append("<tr class='").append(operation).append("'>")
                    .append("<td>").append(field).append("</td>")
                    .append("<td>").append(operation.toUpperCase()).append("</td>")
                    .append("<td>").append(oldValue).append("</td>")
                    .append("<td>").append(newValue).append("</td>")
                    .append("</tr>");
        }

        html.append("</table>")
                .append("<div class='container'>")
                .append("<div class='json-box'>")
                .append("<h3>Original JSON</h3><pre>");

        // Farkları vurgulamak için orijinal JSON'u işliyoruz
        beautifiedOriginalJson = highlightDifferences(beautifiedOriginalJson, differences, "replace");
        beautifiedOriginalJson = highlightDifferences(beautifiedOriginalJson, differences, "add");
        beautifiedOriginalJson = highlightDifferences(beautifiedOriginalJson, differences, "remove");

        html.append(beautifiedOriginalJson)
                .append("</pre></div>")
                .append("<div class='json-box'>")
                .append("<h3>Incoming JSON</h3><pre>");

        // Farkları vurgulamak için gelen JSON'u işliyoruz
        beautifiedIncomingJson = highlightDifferences(beautifiedIncomingJson, differences, "replace");
        beautifiedIncomingJson = highlightDifferences(beautifiedIncomingJson, differences, "add");
        beautifiedIncomingJson = highlightDifferences(beautifiedIncomingJson, differences, "remove");

        html.append(beautifiedIncomingJson)
                .append("</pre></div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return html.toString();
    }


    private String highlightDifferences(String json, List<Map<String, Object>> differences, String operation) {
        for (Map<String, Object> diff : differences) {
            if (diff.get("operation").equals(operation)) {
                String field = (String) diff.get("field");
                String value = (String) diff.get("newValue");
                if (json.contains(field)) {
                    json = json.replace(value, "<span style='background-color:" + getColor(operation) + ";'>" + value + "</span>");
                }
            }
        }
        return json;
    }

    private String getColor(String operation) {
        switch (operation) {
            case "replace":
                return "#ffeb3b";  // Yellow for replace
            case "remove":
                return "#f44336";  // Red for remove
            case "add":
                return "#4caf50";  // Green for add
            default:
                return "#ffffff";  // Default to white
        }
    }
}
