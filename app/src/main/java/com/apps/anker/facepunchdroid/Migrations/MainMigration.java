package com.apps.anker.facepunchdroid.Migrations;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Mikkel on 18-05-2016.
 */
public class MainMigration {
    // Example migration adding a new class
    static RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            // DynamicRealm exposes an editable schema
            RealmSchema schema = realm.getSchema();

            // Migrate to version 1: Add a new class.
            // Example:
            // public Person extends RealmObject {
            //     private String name;
            //     private int age;
            //     // getters and setters left out for brevity
            // }
            if (oldVersion == 0) {
                schema.create("UserScript")
                        .addField("title", String.class)
                        .addField("url", String.class);
                oldVersion++;
            }

            if (oldVersion == 1) {
                schema.get("UserScript")
                        .addField("javascript", String.class);
                oldVersion++;
            }
        }
    };

    public static RealmMigration getMigration() {
        return migration;
    }

}
