package com.abp.clock;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.time.*;
import java.io.IOException;

/**
 * JavaFX App
 */
public class Clock extends Application {

    public AnimationTimer timer;

    @Override
    public void start(Stage primaryStage) throws IOException {

        ClockPane clock = new ClockPane();

        // Place clock and label in border pane
        BorderPane pane = new BorderPane();
        pane.setCenter(clock);
        pane.setStyle("-fx-background-color: black; -fx-border-color : black; -fx-border-width : 0 5 ");

        // Create a scene and place the pane in the stage
        Scene scene = new Scene(pane, 640, 480);
        scene.setFill(Color.GRAY);
        primaryStage.setTitle("Nolan's Clock"); // Set the stage
        // title===========
        primaryStage.setScene(scene); // Place the scene in the stage

        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            public void handle(long now) {
                if(now - lastUpdate >= 28_000_000) {
                    clock.setCurrentTime();
                    clock.drawClock();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
        primaryStage.show(); // Display the stage

    }


    // =====================

    public class ClockPane extends Pane {
        private int hour;
        private int minute;
        private int second;
        private int day;
        private ImageView imageView = null;
        private Image im = null;

        /** Construct a default clock with the current time */
        public ClockPane() {
            // get the image once
            try {
                im = new Image(getClass().getResourceAsStream("/nolan.jpg"));
            } catch (Exception e) {
                System.out.println(e);
            }
            imageView = new ImageView(im);
            setCurrentTime();
        }

        /** Construct a clock with specified hour, minute, and second */
        public ClockPane(int hour, int minute, int second, int day) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.day = day;
        }

        /** Return hour */
        public int getHour() {
            return hour;
        }

        /** Return minute */
        public int getMinute() {
            return minute;
        }

        /** Return second */
        public int getSecond() {
            return second;
        }


        /* Set the current time for the clock */
        public void setCurrentTime() {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
            this.hour = zdt.getHour();
            this.minute = zdt.getMinute();
            this.second = zdt.getSecond();
            this.day = zdt.getDayOfMonth();
        }

        /** Paint the clock */
        private void drawClock() {
            // Initialize clock parameters
            // radius of circle to fit in pane
            double clockRadius = Math.min(getWidth(), getHeight()) * 0.8 * 0.5;
            double centerX = getWidth() / 2;
            double centerY = getHeight() / 2;

            // Draw circle
            Circle circle = new Circle(centerX, centerY, clockRadius);
            circle.setFill(new ImagePattern(im)); // =====changed color==============
            circle.setStroke(Color.BLACK);

            imageView.setPreserveRatio(true);
            imageView.setFitHeight(500);
            imageView.setFitWidth(500);

            Circle innerCircle = new Circle(centerX, centerY, 2);
            innerCircle.setFill(Color.BLACK); // =====changed color==============
            innerCircle.setStroke(Color.BLACK);


            String dayToString = String.valueOf(day);
            Text dayText = new Text(dayToString);
            dayText.setX(centerX+clockRadius-57);
            dayText.setY(centerY+5);
            dayText.setFill(Color.WHITE);

            // rectangle fpr the day of month
            Rectangle dayRect = new Rectangle(centerX+clockRadius-60, centerY-7, 23,15);
            dayRect.setFill(Color.BLACK);

            // Draw second hand
            // magic number for calculating 1 radian
            double RPM = 2 * (Math.PI / 60); // 1 radian
            double sLength = clockRadius * 0.8;
            double secondX = centerX + (sLength * Math.sin(second * RPM));
            double secondY = centerY - (sLength * Math.cos(second * RPM));

            Line sLine = new Line(centerX, centerY, secondX, secondY);
            sLine.setStroke(Color.WHITE);
            sLine.setStrokeWidth(2.0);

            // Draw minute hand
            // not all the way to the actual circle
            double mLength = clockRadius * 0.65;
            // sin of minute * radian
            double xMinute = centerX + (mLength * Math.sin(minute * RPM));
            // cosine of minute * radian
            double minuteY = centerY - (mLength * Math.cos(minute * RPM));
            Line mLine = new Line(centerX, centerY, xMinute, minuteY);
            mLine.setStroke(Color.BLACK); // changed color to brown======================
            mLine.setStrokeWidth(3.0);
            // Draw hour hand
            double hLength = clockRadius * 0.5;
            double hourX = centerX + hLength *
                    Math.sin((hour % 12 + minute / 60.0) * (2 * Math.PI / 12));
            double hourY = centerY - hLength *
                    Math.cos((hour % 12 + minute / 60.0) * (2 * Math.PI / 12));
            Line hLine = new Line(centerX, centerY, hourX, hourY);
            hLine.setStroke(Color.BLACK);
            hLine.setStrokeWidth(3.0);

            getChildren().clear(); // Clear the pane
            // add all circle, minute, second and hour hands
            // also add the day of month stuff
            getChildren().addAll(circle, sLine, mLine, hLine, dayRect, dayText);
            getChildren().add(innerCircle);

            Group ticks = new Group();// create tick hands============================
            Group numbers = new Group(); // create numbers==========================

            // creating the big ticks (12)===============================

            for (int i = 0; i < 12; i++) {
                /*
                 * creating a line with a width of 10 and placing at 'clockRadius'
                 * distance away from center
                 */
                Line tick = new Line(0, clockRadius, 0, clockRadius - 10);
                tick.setStroke(Color.WHITE);
                tick.setTranslateX(centerX);
                tick.setTranslateY(centerY);
                // applying proper rotation to rotate the tick
                // this was interesting to get the tick correct
                tick.getTransforms().add(new Rotate(i * (360 / 12)));
                // adding to ticks group
                ticks.getChildren().add(tick);

            }

            // creating the small ticks=========================================

            for (int i = 0; i < 60; i++) {
                // lines will have a width of 5
                Line tick = new Line(0, clockRadius, 0, clockRadius - 5);
                tick.setStroke(Color.WHITE);
                tick.setTranslateX(centerX);
                tick.setTranslateY(centerY);
                tick.getTransforms().add(new Rotate(i * (360 / 60)));
                ticks.getChildren().add(tick);
            }

            // creating the numbers==================================================

            int num = 12; // starting with 12
            for (int i = 0; i < 12; i++) {
                // finding proper position x and y by applying the equation
                // found this formula online
                double x = centerX + (clockRadius - 20) * Math.sin((i % 12) * (2 * Math.PI / 12));
                double y = centerY - (clockRadius - 20) * Math.cos((i % 12) * (2 * Math.PI / 12));
                // defining a text with hour label, (x-5 and y+5 are used to align text
                // in proper position, considering font height & width)
                Text t = new Text(x - 5, y + 5, "" + num);
                t.setStroke(Color.WHITE);
                numbers.getChildren().add(t);
                num++;
                if (num > 12) {
                    num = 1;
                }

            }

            // adding ticks and numbers======================
            getChildren().add(ticks);
            getChildren().add(numbers);

        }
    }


    public static void main(String[] args) {
        launch();
    }
}