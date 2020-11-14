package br.gov.sp.fatec.trampoz_api.shared;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class LocationHeaderUtils {
    public static void create(HttpServletResponse res, String url, UUID id) {
        String location = url + "?id=" + id;
        res.setHeader("Location", location);
    }
}
