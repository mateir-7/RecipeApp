<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="userName" />

    <xsl:template match="/">
        <html>
            <head>
                <title>All Recipes</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f9; }
                    a { display: inline-block; margin-bottom: 20px; text-decoration: none; color: #0056b3; font-weight: bold; }
                    .recipe { border: 1px solid #ccc; padding: 15px; margin-bottom: 10px; border-radius: 8px; }
                    .yellow-bg { background-color: #fff3cd; border-color: #ffeeba; } /* Softer, nicer yellow */
                    .green-bg { background-color: #d4edda; border-color: #c3e6cb; } /* Softer, nicer green */
                </style>
            </head>
            <body>
                <h1>Recipe List for User: <xsl:value-of select="$userName"/></h1>
                <a href="/">&#8592; Go Back to Dashboard</a>

                <xsl:variable name="userSkill" select="/App/Users/User[Name=$userName]/SkillLevel" />

                <xsl:for-each select="App/Recipes/Recipe">
                    <xsl:choose>
                        <xsl:when test="DifficultyLevels/Difficulty = $userSkill">
                            <div class="recipe yellow-bg">
                                <h3><xsl:value-of select="Title"/></h3>
                                <p><b>Cuisines:</b>
                                    <xsl:for-each select="CuisineTypes/Cuisine">
                                        <xsl:value-of select="."/>
                                        <xsl:if test="position() != last()"> + </xsl:if>
                                    </xsl:for-each>
                                </p>
                                <p><b>Difficulty:</b> <xsl:value-of select="DifficultyLevels/Difficulty"/></p>
                            </div>
                        </xsl:when>
                        <xsl:otherwise>
                            <div class="recipe green-bg">
                                <h3><xsl:value-of select="Title"/></h3>
                                <p><b>Cuisines:</b>
                                    <xsl:for-each select="CuisineTypes/Cuisine">
                                        <xsl:value-of select="."/>
                                        <xsl:if test="position() != last()"> + </xsl:if>
                                    </xsl:for-each>
                                </p>
                                <p><b>Difficulty:</b> <xsl:value-of select="DifficultyLevels/Difficulty"/></p>
                            </div>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>