package com.khovaylo.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Основной класс, с необходимыми методами для получения информации о товарах по запросу
 * @author Pavel Khovaylo
 */
public class Application {

    /**
     * Метод по получению объекта BufferedReader (читает текст из потока ввода символов) из http-запроса,
     * возвращающего данные в формате json
     * @param limit количество товаров в запросе (не более 40 товаров)
     * @param offset смещение
     * @return объекта BufferedReader
     */
    private Reader getReaderFromUrl(int limit, int offset) {
        String requestURL = "https://gpsfront.aliexpress.com/getRecommendingResults.do?callback=jQuery18304448626992519271_1615039477717&widget_id=5547572&platform=pc&limit=" + limit + "&offset=" + offset + "&phase=1&productIds2Top=&postback=d01662bf-8801-4098-9de0-85607712e3cc&_=1615039939479";
        BufferedReader bufferedReader = null;
        try {
            URL aliRequest = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) aliRequest.openConnection();
            connection.setRequestMethod("GET");
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return getReader(bufferedReader);
    }

    /**
     * Метод помещает данные в строку из объекта BufferedReader
     * @param bufferedReader объект BufferedReader, хранящий информацию из http-запроса
     * @return строка содержащая информацию из объекта BufferedReader
     */
    private String createJsonFromReader(BufferedReader bufferedReader) {
        StringBuilder stringBuilder = new StringBuilder();
        String input;

        if (bufferedReader != null) {
            try {
                while ((input = bufferedReader.readLine()) != null){
                    stringBuilder.append(input);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return getCorrectStringFromStringBuilder(stringBuilder);
    }

    /**
     * При помощи данного метода убираем лишние символы из запроса (комментарии и т.п.) для корректного преобразования
     * полученной информации в объект JSONTokener
     * @param stringBuilder объект StringBuilder, хранящий символьную информацию
     * @return корректная строка, содержащая информацию в виде json
     */
    private String getCorrectStringFromStringBuilder(StringBuilder stringBuilder) {
        int startIndex = stringBuilder.indexOf("{");
        return stringBuilder.substring(startIndex);
    }

    /**
     * Метод создает объект StringReader из строковой информации
     * @param bufferedReader объект BufferedReader, хранящий информацию из http-запроса
     * @return объект StringReader
     */
    private Reader getReader(BufferedReader bufferedReader) {
        String stringJson = createJsonFromReader(bufferedReader);
        return new StringReader(stringJson);
    }

    /**
     * Метод создает объект JSONObject из объекта Reader
     * Затем находим в данном json-объекте информацию о товарах, используя ключ "results"
     * Информация выглядит в виде массива json-объектов
     * @param limit количество товаров в запросе (не более 40 товаров) - см. метод getReaderFromUrl(int limit, int offset)
     * @param offset смещение - см. метод getReaderFromUrl(int limit, int offset)
     * @return объект JSONArray, содержащий массив json-объектов (товаров)
     */
    private JSONArray getJSONArray(int limit, int offset) {
        JSONTokener jsonTokener = new JSONTokener(getReaderFromUrl(limit, offset));
        JSONObject jsonObject = new JSONObject(jsonTokener);
        return (jsonObject.get("results") != null) ? jsonObject.getJSONArray("results") : null;
    }

    /**
     * Метод парсит строку и создает объект Double
     * @param s входящая строка
     * @return объект Double
     */
    private static Double getDoubleFromString(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i]) || chars[i] == '.') {
                stringBuilder.append(chars[i]);
            }
        }
        return Double.valueOf(stringBuilder.toString());
    }

    /**
     * Метод переводит секунды в объект ZonedDateTime
     * @param seconds количество секунд
     * @return объект ZonedDateTime
     */
    private static ZonedDateTime getZdtFromSecond(Long seconds) {
        Instant instant = Instant.ofEpochSecond(seconds);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Метод, в котором мы проходимся по массиву json-объектов, преобразуя информацию о товаре в объект Product{@link Product}
     * @param limit количество товаров в запросе (не более 40 товаров) - см. метод getReaderFromUrl(int limit, int offset)
     * @param offset смещение - см. метод getReaderFromUrl(int limit, int offset)
     * @return список объектов Product{@link Product}
     */
    private List<Product> getProductList(int limit, int offset) {
        List<Product> productList = new ArrayList<>();
        JSONArray jsonArray = getJSONArray(limit, offset);

        for (int i = 0; i < (jsonArray != null ? jsonArray.length() : 0); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            Product product = new Product(
                    json.getString("productTitle"),
                    getDoubleFromString(json.getString("oriMinPrice")),
                    getDoubleFromString(json.getString("oriMaxPrice")),
                    getDoubleFromString(json.getString("minPrice")),
                    getDoubleFromString(json.getString("maxPrice")),
                    getDoubleFromString(json.getString("discount")),
                    Integer.valueOf(json.getString("stock")),
                    Integer.valueOf(json.getString("orders")),
                    Integer.valueOf(json.getString("totalTranpro3")),
                    Double.valueOf(json.getString("productAverageStar")),
                    json.getInt("itemEvalTotalNum"),
                    getZdtFromSecond(json.getLong("gmtCreate")),
                    getZdtFromSecond(json.getLong("startTime")),
                    getZdtFromSecond(json.getLong("endTime")));
            productList.add(product);
        }

        return productList;
    }

    /**
     * Метод, возвращающий полный список объектов из нескольких http-запросов (в одном запросе не более 40 товаров)
     * @param quantityProduct требуемое количество товаров (на основании этой информации будет создано
     *                       определенное количество http-запросов)
     * @return полный список объектов Product{@link Product}
     */
    private List<Product> getFullList(int quantityProduct) {
        List<Product> result = new ArrayList<>();

        for (int i = 0; i < quantityProduct; i = i + 40) {
            if (quantityProduct >= 40) {
                int different = quantityProduct - i;
                if (different <= 40) {
                    result.addAll(getProductList(different, i));
                } else {
                    result.addAll(getProductList(40, i));
                }
            } else {
                result.addAll(getProductList(quantityProduct, i));
            }
        }
        return result;
    }

    /**
     * Метод преобразует список объектов Product{@link Product} в строку в формате json
     * @param quantityProduct требуемое количество товаров - см. метод getFullList(int quantityProduct)
     * @return строка в формате json с информацией о товарах
     */
    private String getStringJsonOfFullList(int quantityProduct) {
        String json = null;
        try {
            json = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writerWithDefaultPrettyPrinter().writeValueAsString(getFullList(quantityProduct));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Метод записывает информацию из строки в файл с расширением ".csv" и помещает его в директорию target/classes/..
     */
    public void writeJsonToFile() {
        String json = getStringJsonOfFullList(100);
        String dir = Application.class.getResource("/").getFile();
        OutputStream os = null;
        try {
            os = new FileOutputStream(dir + "/product_info.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream printStream = new PrintStream(os);
        printStream.println(json);
        printStream.close();
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.writeJsonToFile();
    }
}