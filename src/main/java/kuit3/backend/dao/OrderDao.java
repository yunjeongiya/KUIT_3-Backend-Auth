package kuit3.backend.dao;

import kuit3.backend.dto.order.GetOrderResponse;
import kuit3.backend.dto.user.GetUserResponse;
import kuit3.backend.dto.user.PostUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class OrderDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

//    public long createOrder(PostOrderRequest postOrderRequest) {
//        String sql = "insert into user(email, password, phone_number, nickname, profile_image) " +
//                "values(:email, :password, :phoneNumber, :nickname, :profileImage)";
//
//        SqlParameterSource param = new BeanPropertySqlParameterSource(postOrderRequest);
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(sql, param, keyHolder);
//
//        return Objects.requireNonNull(keyHolder.getKey()).longValue();
//    }

    public List<GetOrderResponse> getOrdersByUserId(long userId, long lastSeenId) {
        String sql = "select status, store_id, user_id from orders " +
                "where user_id like :user_id" +
                "and id < :last_seen_id" +
                "order by id desc" +
                "limit :limit";

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("user_id", userId);
        param.addValue("last_seen_id", lastSeenId);
        param.addValue("limit", 100);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetOrderResponse(
                        rs.getString("status"),
                        rs.getLong("store_id"),
                        rs.getLong("user_id"))
        );
    }

    public int modifyOrderStatus_canceled(long orderId) {
        String sql = "update orders set status=:status where id=:order_id";
        Map<String, Object> param = Map.of(
                "status", "canceled",
                "order_id", orderId);
        return jdbcTemplate.update(sql, param);
    }

    public int modifyOrderStatus_completed(long orderId) {
        String sql = "update orders set status=:status where id=:order_id";
        Map<String, Object> param = Map.of(
                "status", "completed",
                "order_id", orderId);
        return jdbcTemplate.update(sql, param);
    }
}