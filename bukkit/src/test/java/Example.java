import com.oglofus.protection.api.value.Value;

import java.io.IOException;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 06/06/2017.
 */
public class Example {
    public static void main(String[] args) throws IOException {
        Value<String> value = Value.<String>builder("test")
                .permission("com.oglofus.permission")
                .def(false)
                .value("Hello World")
                .build();

        System.out.println(value);
    }
}
