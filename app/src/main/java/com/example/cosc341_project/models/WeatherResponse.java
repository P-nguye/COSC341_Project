package com.example.cosc341_project.models;

import java.util.List;

public class WeatherResponse {
    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Rain rain;
    public Clouds clouds;
    public long dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;

    public static class Coord {
        public double lon;
        public double lat;
    }

    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public static class Main {
        public float temp;
        public float feels_like;
        public float temp_min;
        public float temp_max;
        public int pressure;
        public int humidity;
        public int sea_level;
        public int grnd_level;
    }

    public static class Wind {
        public float speed;
        public int deg;
        public float gust;
    }

    public static class Rain {
        public float _1h; // For "1h" field
    }

    public static class Clouds {
        public int all;
    }

    public static class Sys {
        public int type;
        public int id;
        public String country;
        public long sunrise;
        public long sunset;
    }
}
