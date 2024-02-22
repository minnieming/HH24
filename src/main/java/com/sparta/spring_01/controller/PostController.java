package com.sparta.spring_01.controller;

import com.sparta.spring_01.dto.PostRequestDto;
import com.sparta.spring_01.dto.PostResponseDto;
import com.sparta.spring_01.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@Controller
@RequestMapping("/api")
public class PostController {

    private final JdbcTemplate jdbcTemplate;

    public PostController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 게시글 생성 / 작성
    @PostMapping ("/post")
    public PostResponseDto createPost (@RequestBody PostRequestDto requestDto){
        // RequestDto -> Entity
        Post post = new Post(requestDto);

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO post (book, writer, price) VALUES (?, ?, ?)";
        jdbcTemplate.update( con -> { // 4. insert문 사용할때는 update를 사용해서 요청한다. (insert, update, delete 다 update 사용)
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getBook()); // 2. preparedStatement.setString 이 메서드로 ?의 1번째 자리에 들어갈것, 2번째 자리에 들어갈것을 넣어준다.
                    preparedStatement.setString(2, post.getWriter()); // 3. 여기서 memo는 위에 인스턴스화 해준 memo에서 가져온 것.
                    preparedStatement.setInt(3, post.getPrice());
                    return preparedStatement;
                },
                keyHolder);

        Long id = keyHolder.getKey().longValue(); // 5. 기본키가 필요하기 때문에 만들었던 기본키를 받아와서 넣어 논 것.
        post.setId(id);

        PostResponseDto postResponseDto = new PostResponseDto(post);

        return postResponseDto;

    }

    // 선택한 게시글 조회
    @GetMapping("/post/{postId}")
    public PostResponseDto getPostById(@PathVariable Long postId) {
        String sql = "SELECT * FROM post WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{postId}, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String book = rs.getString("book");
                String writer = rs.getString("writer");
                int price = rs.getInt("price");
                return new PostResponseDto(id, book, writer, price);
            }
        });
    }


    // 게시글 목록 조회
    @GetMapping("/")
    public List<PostResponseDto> getPost() { // 6. 만들어진 필드들이 list형식으로 묶여서 반환이 된다.
        // DB 조회
        String sql = "SELECT * FROM post"; // 1. select문. string값으로 만들어 놓은 것

        return jdbcTemplate.query(sql, new RowMapper<PostResponseDto>() { // 2. select문은 query를 사용한다. (sql은 string으로 만들어 놓은것을 넣은 것 / 뒤에 있는건 밑에 있는 값인듯)
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException { // 3. 여기 데이터가 for문처럼 돌아서 ResultSet에 담긴다
                // SQL 의 결과로 받아온 Memo 데이터들을 MemoResponseDto 타입으로 변환해줄 메서드
                Long id = rs.getLong("id"); // 4. get으로 가져올수 있다. get뒤에 타입을 적어서 가져오면 된다. (해당하는 column의 이름)
                String book = rs.getString("book");
                String writer = rs.getString("writer");
                int price = rs.getInt("price");
                return new PostResponseDto(id, book, writer, price); // 5. 값을 가져오면 MemoResponseDto로 하나로 만들어 지는 것. 이걸로 돌면서 MemoResponseDto의 객체가 만들어진다
            }
        });
    }

    // 게시글 수정
    @PutMapping("/")
    public Long updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto){

        Post post = findById(id);
        if(post != null) {
            // memo 내용 수정
            String sql = "UPDATE post SET book = ?, wirter = ?, price = ? WHERE id = ?"; // 2. DB에 저장할때처럼 ?에 넣고 set해서 쿼리가 요청이 된다. (위에 설명도 참고)
            jdbcTemplate.update(sql, requestDto.getBook(), requestDto.getWriter(), requestDto.getPrice(), id); // 1. update 넣고, sql뒤에 물음표의 순서에 따라 괄호안에 넣어주기

            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
    }

    // 게시글 삭제
    @DeleteMapping("/")
    public Long deletePost(@PathVariable Long id) {

        Post post = findById(id);
        if(post != null) {
            // memo 삭제
            String sql = "DELETE FROM post WHERE id = ?";
            jdbcTemplate.update(sql, id); // 2. update 사용해서 ?자리에 넣어주면 된다. (위의 내용 참고)

            return id;
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    // findById 메서드
    private Post findById(Long id) { // DB에 존재하는지 찾는 기능. 메서드로 따로 빼서 여기저기 사용 할 수 있게 해놨다.
        // DB 조회
        String sql = "SELECT * FROM post WHERE id = ?"; // 1. SELECT 문으로 만들었다.

        return jdbcTemplate.query(sql, resultSet -> {
            if(resultSet.next()) {
                Post post = new Post(); // memo타입으로 들어간다 (이건 사용자에 맞게 변경하면 될것 같다)
                post.setBook (resultSet.getString("book"));
                post.setWriter(resultSet.getString("writer"));
                post.setPrice(resultSet.getInt("price"));

                return post;
            } else {
                return null;
            }
        }, id);
    }
}
