package com.recipeapp.semantic_web_hw1;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringWriter;

@RestController
public class RecipeController {
    private File getXmlFile() {
        return new File("data.xml");
    }

    // color coded recipes
    @GetMapping("/recipes")
    @ResponseBody
    public String displayRecipes(@RequestParam String userName) {
        try {
            StreamSource xmlSource = new StreamSource(getXmlFile());
            StreamSource xslSource = new StreamSource(new ClassPathResource("style.xsl").getInputStream());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslSource);

            transformer.setParameter("userName", userName);

            StringWriter writer = new StringWriter();
            transformer.transform(xmlSource, new StreamResult(writer));

            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "<h2>Error loading recipes. Check your console.</h2>";
        }
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String name,
                          @RequestParam String surname,
                          @RequestParam String skillLevel,
                          @RequestParam String preferredCuisine) {

        // validate input
        if (name == null || name.trim().isEmpty() ||
                surname == null || surname.trim().isEmpty() ||
                preferredCuisine == null || preferredCuisine.trim().isEmpty()) {
            return "<h3>Error: All fields are required! Please go back and try again.</h3>";
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(getXmlFile());
            doc.getDocumentElement().normalize();
            Element newUser = doc.createElement("User");

            Element nameElem = doc.createElement("Name");
            nameElem.appendChild(doc.createTextNode(name.trim()));
            newUser.appendChild(nameElem);

            Element surnameElem = doc.createElement("Surname");
            surnameElem.appendChild(doc.createTextNode(surname.trim()));
            newUser.appendChild(surnameElem);

            Element skillElem = doc.createElement("SkillLevel");
            skillElem.appendChild(doc.createTextNode(skillLevel));
            newUser.appendChild(skillElem);

            Element cuisineElem = doc.createElement("PreferredCuisine");
            cuisineElem.appendChild(doc.createTextNode(preferredCuisine.trim()));
            newUser.appendChild(cuisineElem);

            doc.getElementsByTagName("Users").item(0).appendChild(newUser);

            // cleaning whitespace (empty lines) from the xml
            javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
            org.w3c.dom.NodeList emptyTextNodes = (org.w3c.dom.NodeList) xpath.compile("//text()[normalize-space(.)='']")
                    .evaluate(doc, javax.xml.xpath.XPathConstants.NODESET);
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                org.w3c.dom.Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }

            // save to xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // java saves xml files with no lines between data, so we added indentation
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            // this sets indentation amount at 4 spaces per nested tag
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // saves new xml data over old xml
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(getXmlFile());
            transformer.transform(source, result);

            return "<h3>User added successfully!</h3><a href='/'>Go back to Dashboard</a>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<h3>Error saving user data. Check console.</h3>";
        }
    }

    @PostMapping("/addRecipe")
    public String addRecipe(@RequestParam String title,
                            @RequestParam String cuisine1,
                            @RequestParam String cuisine2,
                            @RequestParam String difficulty) {

        // validate input
        if (title == null || title.trim().isEmpty() ||
                cuisine1 == null || cuisine1.trim().isEmpty() ||
                cuisine2 == null || cuisine2.trim().isEmpty() ||
                difficulty == null || difficulty.trim().isEmpty()) {
            return "<h3>Error: All fields are required! Please go back and try again.</h3>";
        }

        try {
            javax.xml.parsers.DocumentBuilderFactory dbFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(getXmlFile());
            doc.getDocumentElement().normalize();

            org.w3c.dom.Element newRecipe = doc.createElement("Recipe");

            org.w3c.dom.Element titleElem = doc.createElement("Title");
            titleElem.appendChild(doc.createTextNode(title.trim()));
            newRecipe.appendChild(titleElem);

            org.w3c.dom.Element cuisinesElem = doc.createElement("CuisineTypes");
            org.w3c.dom.Element c1Elem = doc.createElement("Cuisine");
            c1Elem.appendChild(doc.createTextNode(cuisine1));
            org.w3c.dom.Element c2Elem = doc.createElement("Cuisine");
            c2Elem.appendChild(doc.createTextNode(cuisine2));
            cuisinesElem.appendChild(c1Elem);
            cuisinesElem.appendChild(c2Elem);
            newRecipe.appendChild(cuisinesElem);

            org.w3c.dom.Element diffsElem = doc.createElement("DifficultyLevels");
            org.w3c.dom.Element diffElem = doc.createElement("Difficulty");
            diffElem.appendChild(doc.createTextNode(difficulty));
            diffsElem.appendChild(diffElem);
            newRecipe.appendChild(diffsElem);

            doc.getElementsByTagName("Recipes").item(0).appendChild(newRecipe);

            // cleaning whitespace (empty lines) from the xml
            javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
            org.w3c.dom.NodeList emptyTextNodes = (org.w3c.dom.NodeList) xpath.compile("//text()[normalize-space(.)='']")
                    .evaluate(doc, javax.xml.xpath.XPathConstants.NODESET);
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                org.w3c.dom.Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }

            // save to xml
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();

            // java saves xml files with no lines between data, so we added indentation
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            // this sets indentation amount at 4 spaces per nested tag
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // saves new xml data over old xml
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(getXmlFile());
            transformer.transform(source, result);

            return "<h3>Recipe added successfully!</h3><a href='/'>Go back to Dashboard</a>";

        } catch (Exception e) {
            e.printStackTrace();
            return "<h3>Error saving recipe data. Check console.</h3>";
        }
    }

    // XPath queries
    private String executeRecipeXPath(String xpathExpression, String title) {
        StringBuilder html = new StringBuilder("<html><head><title>" + title + "</title>");
        html.append("<style>body{font-family:Arial, sans-serif; margin:40px; background-color:#f4f4f9;}");
        html.append(".recipe{background:white; border:1px solid #ccc; padding:15px; margin-bottom:10px; border-radius:8px;}");
        html.append("a{display:inline-block; margin-bottom:20px; text-decoration:none; color:#0056b3; font-weight:bold;}</style></head><body>");
        html.append("<h1>").append(title).append("</h1><a href='/'>&larr; Go Back to Dashboard</a>");

        try {
            javax.xml.parsers.DocumentBuilderFactory dbFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(getXmlFile());

            javax.xml.xpath.XPathFactory xPathfactory = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xpath = xPathfactory.newXPath();
            javax.xml.xpath.XPathExpression expr = xpath.compile(xpathExpression);

            org.w3c.dom.NodeList nl = (org.w3c.dom.NodeList) expr.evaluate(doc, javax.xml.xpath.XPathConstants.NODESET);

            if (nl.getLength() == 0) {
                html.append("<p>No recipes found matching these criteria.</p>");
            } else {
                for (int i = 0; i < nl.getLength(); i++) {
                    org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);
                    String recTitle = el.getElementsByTagName("Title").item(0).getTextContent();

                    org.w3c.dom.NodeList cuisines = el.getElementsByTagName("Cuisine");
                    String c1 = cuisines.item(0).getTextContent();
                    String c2 = cuisines.item(1).getTextContent();

                    String diff = el.getElementsByTagName("Difficulty").item(0).getTextContent();

                    html.append("<div class='recipe'>");
                    html.append("<h3>").append(recTitle).append("</h3>");
                    html.append("<p><b>Cuisines:</b> ").append(c1).append(" + ").append(c2).append("</p>");
                    html.append("<p><b>Difficulty:</b> ").append(diff).append("</p>");
                    html.append("</div>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            html.append("<p>Error executing XPath query. Check console.</p>");
        }
        html.append("</body></html>");
        return html.toString();
    }

    // recommendations based on the chosen user's skill level
    @GetMapping("/recommendSkill")
    @ResponseBody
    public String recommendBySkill(@RequestParam String userName) {
        String query = "//Recipe[DifficultyLevels/Difficulty = //Users/User[Name='" + userName + "']/SkillLevel]";
        return executeRecipeXPath(query, "Recommendations based on " + userName + "'s Skill Level");
    }

    // recommendations based on the chosen user's skill level + cuisine
    @GetMapping("/recommendSkillAndCuisine")
    @ResponseBody
    public String recommendBySkillAndCuisine(@RequestParam String userName) {
        String query = "//Recipe[DifficultyLevels/Difficulty = //Users/User[Name='" + userName + "']/SkillLevel and CuisineTypes/Cuisine = //Users/User[Name='" + userName + "']/PreferredCuisine]";
        return executeRecipeXPath(query, "Recommendations for " + userName + " (Skill + Cuisine)");
    }

    // see details of a specific recipe by title
    @GetMapping("/searchRecipe")
    @ResponseBody
    public String searchRecipe(@RequestParam String title) {
        String query = "//Recipe[Title='" + title + "']";
        return executeRecipeXPath(query, "Search Results for: " + title);
    }

    // filter recipes by a selected cuisine type
    @GetMapping("/filterCuisine")
    @ResponseBody
    public String filterCuisine(@RequestParam String cuisine) {
        String query = "//Recipe[CuisineTypes/Cuisine='" + cuisine + "']";
        return executeRecipeXPath(query, "Recipes matching Cuisine: " + cuisine);
    }

    // sends us back to dashboard
    @GetMapping("/")
    public ModelAndView showDashboard() {
        return new ModelAndView("index");
    }
}