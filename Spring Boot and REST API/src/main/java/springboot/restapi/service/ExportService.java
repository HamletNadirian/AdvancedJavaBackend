package springboot.restapi.service;

import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import springboot.restapi.data.Movie;
import springboot.restapi.dto.movie.MovieListRequestDto;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.text.StringEscapeUtils.escapeCsv;


@Service
@AllArgsConstructor
public class ExportService {
    private final MovieService movieService;

    public Resource exportToCSV(MovieListRequestDto request) {
        List<Movie> movies = movieService.getMoviesForExport(request);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            String[] headers = {"ID", "Title", "Release Date", "Producer", "Genre", "Rating"};
            csvWriter.writeNext(headers);

            for (Movie movie : movies) {
                String[] row = new String[]{
                        String.valueOf(movie.getId()),
                        movie.getTitle(),
                        String.valueOf(movie.getReleaseDate()),
                        movie.getProducer().getName(),
                        movie.getGenre(),
                        movie.getRating() != null ? String.format(Locale.US, "%.1f", movie.getRating()) : "0.0"
                };
                csvWriter.writeNext(row);
            }

            csvWriter.flush();
            byte[] data = baos.toByteArray();
            return new ByteArrayResource(data);

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }
}
