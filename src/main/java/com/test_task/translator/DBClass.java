package com.test_task.translator;

import com.test_task.translator.model.TranslateInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;

import static java.time.LocalDateTime.now;

@Component
public class DBClass {

    @Bean(name = "dbClassBean")
    DBClass dbClass() {
        return new DBClass();
    }

    @Bean
    public DataSource hsqlDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    JdbcTemplate jdbcTemplate = new JdbcTemplate(hsqlDataSource());

    public void createTeble() {
        // Удаляем таблицу, если она есть
        jdbcTemplate.execute("DROP TABLE translate_results IF EXISTS");
        // Создаем таблицу
        jdbcTemplate.execute("CREATE TABLE translate_results(call_time DATE, input_parameters VARCHAR(255), ip_client VARCHAR(255))");
    }

    // Метод записи в базу данных
    public void writeToDB(MultiValueMap params) throws IOException {

        // Для получения корректного ip клиента
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("translate.yandex.net", 80));
        String ipClient = socket.getLocalAddress().toString();
        socket.close();

        // Вставляем данные запроса в таблицу
        jdbcTemplate.update("INSERT INTO translate_results(call_time, input_parameters, ip_client) VALUES (?,?,?)", Timestamp.valueOf(now()), "text: " + params.get("text") + ", key: "
                + params.get("key") + ", lang: "
                + params.get("lang"), ipClient.replace("/", ""));
    }

    // Метод чтения данных из базы
    public void readFromDB() {
        // Запрос всех данных таблицы
        String sql = "SELECT * FROM translate_results";

        // Получаем результат запроса
        List<TranslateInfo> fromDB = jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new TranslateInfo(
                                rs.getTimestamp("call_time"),
                                rs.getString("input_parameters"),
                                rs.getString("ip_client")
                        )
        );

        // Выводим результат запроса
        for (TranslateInfo trInfoRow : fromDB) {
            System.out.println(trInfoRow);
        }
    }
}
