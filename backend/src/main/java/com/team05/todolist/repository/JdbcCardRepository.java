package com.team05.todolist.repository;

import com.team05.todolist.domain.Card;
import com.team05.todolist.domain.Section;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCardRepository implements CardRepository {

    private static final int NON_DELETED = 0;
    private static final int DELETED = 1;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    public JdbcCardRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("card")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public int save(Card card) {
        if (card.getId() != null) {
            jdbcTemplate.update(
                "UPDATE card SET order_index=?, title=?, content=?, section=?, author=? WHERE id=?",
                card.getOrder(), card.getTitle(), card. getContent(), card.getSectionType(), card.getAuthor(), card.getId());

            return card.getId();
        }

        Map<String, Object> params = getSaveParams(card);
        return simpleJdbcInsert.executeAndReturnKey(params).intValue();
    }

    private Map<String, Object> getSaveParams(Card card) {
        Map<String, Object> params = new HashMap<>();
        params.put("order_index", card.getOrder());
        params.put("delete_yn", card.getDeleteYN());
        params.put("title", card.getTitle());
        params.put("content", card.getContent());
        params.put("section", card.getSectionType());
        params.put("author", card.getAuthor());
        return params;
    }

    @Override
    public int delete(int id) {
        jdbcTemplate.update("UPDATE card SET delete_yn=? WHERE id=?", DELETED, id);
        return id;
    }

    @Override
    public List<Card> findAll() {
        return jdbcTemplate.query(
            "SELECT id, order_index, delete_yn, title, content, section, author FROM card where delete_yn = ?",
            cardRowMapper(), NON_DELETED);
    }

    @Override
    public Optional<Card> findById(int id) {
        List<Card> result = jdbcTemplate.query(
            "SELECT id, order_index, delete_yn, title, content, section, author FROM card WHERE id = ?",
            cardRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public Integer findNumberOfCards(String section) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM card WHERE section=? and delete_yn=?", Integer.class, section, NON_DELETED);
    }

    @Override
    public void move(Card card) {
        jdbcTemplate.update("UPDATE card SET order_index=?, section=? WHERE id=?",
            card.getOrder(), card.getSectionType(), card.getId());
    }

    @Override
    public List<Card> findBySection(Section section) {
        return jdbcTemplate.query(
            "SELECT id, order_index, delete_yn, title, content, section, author FROM card WHERE section=? AND delete_yn=? ORDER BY order_index",
            cardRowMapper(), section.getSectionType(), NON_DELETED);
    }

    private RowMapper<Card> cardRowMapper() {
        return (rs, rowNum) -> {
            Card card = new Card(
                rs.getInt("order_index"),
                rs.getInt("delete_yn"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("section"),
                rs.getString("author")
            );
            card.setId(rs.getInt("id"));
            return card;
        };
    }
}
