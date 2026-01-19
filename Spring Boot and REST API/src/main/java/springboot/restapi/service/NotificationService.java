package springboot.restapi.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springboot.restapi.data.Movie;
import springboot.restapi.data.Producer;
import springboot.restapi.dto.EmailNotificationDto;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailProducer emailProducer;

    @Value("${app.email.admin:admin@movies.com}")
    private String adminEmail;

    @Value("${app.email.from:noreply@movies.com}")
    private String fromEmail;

    public void sendMovieCreatedNotification(Movie movie) {
        EmailNotificationDto emailDto = new EmailNotificationDto();
        emailDto.setOwnerRef("Movie Service");
        emailDto.setEmailFrom(fromEmail);
        emailDto.setEmailTo(adminEmail);
        emailDto.setSubject("New Movie Added: " + movie.getTitle());
        emailDto.setText(String.format(
                "A new movie has been added to the system:\n\n" +
                        "Title: %s\n" +
                        "Release Year: %d\n" +
                        "Genre: %s\n" +
                        "Producer: %s\n" +
                        "Description: %s\n" +
                        "Rating: %s\n\n" +
                        "Created at: %s",
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getGenre(),
                movie.getProducer().getName(),
                movie.getDescription() != null ? movie.getDescription() : "N/A",
                movie.getRating() != null ? movie.getRating().toString() : "N/A",
                movie.getCreatedAt()
        ));
        emailDto.setEntityType("MOVIE");
        emailDto.setEntityId(movie.getId());
        emailDto.setEntityName(movie.getTitle());
        emailDto.setAction("CREATED");

        emailProducer.sendEmailNotification(emailDto);
    }

    public void sendProducerCreatedNotification(Producer producer) {
        EmailNotificationDto emailDto = new EmailNotificationDto();
        emailDto.setOwnerRef("Movie Service");
        emailDto.setEmailFrom(fromEmail);
        emailDto.setEmailTo(adminEmail);
        emailDto.setSubject("New Producer Added: " + producer.getName());
        emailDto.setText(String.format(
                "A new producer has been added to the system:\n\n" +
                        "Name: %s\n" +
                        "Country: %s\n",
                producer.getName(),
                producer.getCountry() != null ? producer.getCountry() : "N/A"
        ));
        emailDto.setEntityType("PRODUCER");
        emailDto.setEntityId(producer.getId());
        emailDto.setEntityName(producer.getName());
        emailDto.setAction("CREATED");

        emailProducer.sendEmailNotification(emailDto);
    }

    public void sendMovieDeletedNotification(Long movieId, String movieTitle) {
        EmailNotificationDto emailDto = new EmailNotificationDto();
        emailDto.setOwnerRef("Movie Service");
        emailDto.setEmailFrom(fromEmail);
        emailDto.setEmailTo(adminEmail);
        emailDto.setSubject("Movie Deleted: " + movieTitle);
        emailDto.setText(String.format(
                "Movie has been deleted from the system:\n\n" +
                        "ID: %d\n" +
                        "Title: %s\n",
                movieId,
                movieTitle
        ));
        emailDto.setEntityType("MOVIE");
        emailDto.setEntityId(movieId);
        emailDto.setEntityName(movieTitle);
        emailDto.setAction("DELETED");

        emailProducer.sendEmailNotification(emailDto);
    }

    public void sendProducerDeletedNotification(Long producerId, String producerName) {
        EmailNotificationDto emailDto = new EmailNotificationDto();
        emailDto.setOwnerRef("Movie Service");
        emailDto.setEmailFrom(fromEmail);
        emailDto.setEmailTo(adminEmail);
        emailDto.setSubject("Producer Deleted: " + producerName);
        emailDto.setText(String.format(
                "Producer has been deleted from the system:\n\n" +
                        "ID: %d\n" +
                        "Name: %s\n",
                producerId,
                producerName
        ));
        emailDto.setEntityType("PRODUCER");
        emailDto.setEntityId(producerId);
        emailDto.setEntityName(producerName);
        emailDto.setAction("DELETED");

        emailProducer.sendEmailNotification(emailDto);
    }
}