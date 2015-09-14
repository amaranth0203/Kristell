package com.mycompany;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import de.greenrobot.daogenerator.DaoGenerator ;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    private static final String GENERATED_SRC_DIR = "./app/gen-src";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, MyDaoGenerator.class.getPackage().getName());
        Entity entity = schema.addEntity("User");
        entity.addLongProperty("UserId").notNull();
        entity.addStringProperty("Password").notNull();
        entity.addStringProperty("Name").notNull();
        createDirs(Paths.get(GENERATED_SRC_DIR));
        new DaoGenerator().generateAll(schema, GENERATED_SRC_DIR);
    }

    private static void createDirs(Path path)  throws IOException {
//        if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
//            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
//            FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(perms);
//            Files.createDirectories(path, attrs);
//        }

        File theDir = new File( path.toString() );
        if (!theDir.exists()) {
            System.out.println("creating directory: " + path.toString() );
            boolean result = false;

            try{
                final boolean mkdir_ = theDir.mkdir();
                result = true;
            }catch(SecurityException se){ System.out.println(se.toString()) ;}
            if(result) {
                System.out.println("DIR created");
            }
        }


    }
}
