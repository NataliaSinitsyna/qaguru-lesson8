import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.*;
import static com.codeborne.pdftest.assertj.Assertions.assertThat;

public class ZipFiles {

    ClassLoader cl = ZipFiles.class.getClassLoader();

    @Test
    void zipFiles() throws Exception {
        File zip = new File("src/test/resources/test.zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        ZipEntry pdf = new ZipEntry("reqpreview.pdf");
        ZipEntry xlsx = new ZipEntry("2023-02-06-02-2023 17-33-22.xlsx");
        ZipEntry csv = new ZipEntry("sample1.csv");
        out.putNextEntry(pdf);
        out.putNextEntry(xlsx);
        out.putNextEntry(csv);
        out.closeEntry();
        out.close();
    }


    @Test
    void readFilesInZip() throws Exception {

        try (
                ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("test.zip"))
        ) {
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                if (entry.getName().equals("2023-02-06-02-2023 17-33-22.xlsx")) {
                    try (InputStream stream = getClass().getClassLoader()
                            .getResourceAsStream("2023-02-06-02-2023 17-33-22.xlsx")) {
                        XLS xls = new XLS(stream);
                        assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(2)
                                .getStringCellValue()).contains("01.01.2023");
                    }
                } else if (entry.getName().equals("reqpreview.pdf")) {
                    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("reqpreview.pdf")) {
                        PDF content = new PDF(stream);
                        assertThat(content.text).contains("2310235603");
                    }
                } else if (entry.getName().equals("sample1.csv")) {
                    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("sample1.csv");
                         CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                        List<String[]> content = reader.readAll();
                        org.assertj.core.api.Assertions.assertThat(content.get(0)[0]).contains("Month");
                    }
                }
            }
        }
    }
}
