module com.sms {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    
    exports com.sms.view;
    exports com.sms.controller;
    exports com.sms.model;
    exports com.sms.dao;
    exports com.sms.util;
    
    opens com.sms.view to javafx.graphics;
    opens com.sms.model to javafx.base;
} 