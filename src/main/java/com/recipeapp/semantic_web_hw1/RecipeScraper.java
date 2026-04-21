package com.recipeapp.semantic_web_hw1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecipeScraper {
    public static void main(String[] args) {
        String url = "https://www.bbcgoodfood.com/recipes/collection/budget-autumn";
        List<String> titles = new ArrayList<>();

        // predefined arrays for random assigning
        String[] allCuisines = {"Italian", "Asian", "Indian", "European", "American", "Mexican", "French", "Mediterranean"};
        // 3 difficulty levels
        String[] allDifficulties = {"Beginner", "Intermediate", "Advanced"};
        Random random = new Random();

        try {
            // Jsoup scraping
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();
            // the site that we scraped uses h2 with the class heading-4
            Elements recipeElements = doc.select("h2.heading-4");
            // we had a problem with some titles so we remove every title starting with "App only"
            for (Element element : recipeElements) {
                String title = element.text();
                if (title.startsWith("App only")) {
                    continue;
                }
                // we also had a problem with the symbol "&" so we just replaced it with "and"
                title = title.replace("&", "and");
                if (!title.isEmpty() && titles.size() < 20) {
                    titles.add(title);
                }
            }
            for (String title : titles) {
                // randomly pick 2 cuisine types for each recipe
                List<String> selectedCuisines = new ArrayList<>();
                while (selectedCuisines.size() < 2) {
                    String randomCuisine = allCuisines[random.nextInt(allCuisines.length)];
                    if (!selectedCuisines.contains(randomCuisine)) {
                        selectedCuisines.add(randomCuisine);
                    }
                }
                String selectedDifficulty = allDifficulties[random.nextInt(allDifficulties.length)];
                // print for XML format
                System.out.println("        <Recipe>");
                System.out.println("            <Title>" + title + "</Title>");
                System.out.println("            <CuisineTypes>");
                System.out.println("                <Cuisine>" + selectedCuisines.get(0) + "</Cuisine>");
                System.out.println("                <Cuisine>" + selectedCuisines.get(1) + "</Cuisine>");
                System.out.println("            </CuisineTypes>");
                System.out.println("            <DifficultyLevels>");
                System.out.println("                <Difficulty>" + selectedDifficulty + "</Difficulty>");
                System.out.println("            </DifficultyLevels>");
                System.out.println("        </Recipe>");
            }

        } catch (IOException e) {
            System.err.println("Error scraping the website: " + e.getMessage());
        }
    }
}