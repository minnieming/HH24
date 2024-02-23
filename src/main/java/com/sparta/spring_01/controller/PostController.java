package com.sparta.spring_01.controller;

import com.sparta.spring_01.dto.PostRequestDto;
import com.sparta.spring_01.dto.PostResponseDto;
import com.sparta.spring_01.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
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

        String sql = "INSERT INTO post (title, username, password, content, date) VALUES (?, ?, ?, ?, ?)"; // 비밀번호를 데이터베이스에는 저장하지만, 밑에는 쓰지 않아 반환된 'PostResponseDto'에는 비밀번호 전달 안됨.
        jdbcTemplate.update( con -> { // 4. insert문 사용할때는 update를 사용해서 요청한다. (insert, update, delete 다 update 사용)
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getTitle()); // 2. preparedStatement.setString 이 메서드로 ?의 1번째 자리에 들어갈것, 2번째 자리에 들어갈것을 넣어준다.
                    preparedStatement.setString(2, post.getUsername()); // 3. 여기서 memo는 위에 인스턴스화 해준 memo에서 가져온 것.
                    preparedStatement.setString(3, post.getPassword()); // 이걸 빼면 오류가 생긴다. DB에는 저장되지만 PostResponseDto에서 비밀번호 필드를 제외 했으므로 반환된 객체에는 비밀번호가 포함되지 않는다.
                    preparedStatement.setString(4, post.getContent());
                    preparedStatement.setObject(5, post.getDate()); // LocalDate는 JDBC에서 직접적으로 지원하는 데이터 유형이 아님. setObject 사용.
                    return preparedStatement;
                },
                keyHolder);

        Long id = keyHolder.getKey().longValue(); // 5. 기본키가 필요하기 때문에 만들었던 기본키를 받아와서 넣어 논 것.
        post.setId(id);

        PostResponseDto postResponseDto = new PostResponseDto(post);

        return postResponseDto;

    }

    // 선택한 게시글 조회 (chatGPT가 만든거)
    @GetMapping("/post/{postId}")
    public PostResponseDto getPostById(@PathVariable Long postId) {
        String sql = "SELECT * FROM post WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{postId}, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String username = rs.getString("username");
                String content = rs.getString("content");
                LocalDate date = rs.getObject("date", LocalDate.class);
                return new PostResponseDto(id, title, username, content, date);
            }
        });
    }


    // 게시글 목록 조회
    @GetMapping("/posts")
    public List<PostResponseDto> getPost() { // 6. 만들어진 필드들이 list형식으로 묶여서 반환이 된다.
        // DB 조회
        String sql = "SELECT * FROM post ORDER BY date DESC"; // 1. select문. string값으로 만들어 놓은 것 // ORDER BY date DESC를 통해 작성일 기준으로 내림차순 정렬 한다.

        return jdbcTemplate.query(sql, new RowMapper<PostResponseDto>() { // 2. select문은 query를 사용한다. (sql은 string으로 만들어 놓은것을 넣은 것 / 뒤에 있는건 밑에 있는 값인듯)
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException { // 3. 여기 데이터가 for문처럼 돌아서 ResultSet에 담긴다
                // SQL 의 결과로 받아온 Memo 데이터들을 MemoResponseDto 타입으로 변환해줄 메서드
                Long id = rs.getLong("id"); // 4. get으로 가져올수 있다. get뒤에 타입을 적어서 가져오면 된다. (해당하는 column의 이름)
                String title = rs.getString("title");
                String username = rs.getString("username");
                String content = rs.getString("content");
                LocalDate date = rs.getObject("date", LocalDate.class);
                return new PostResponseDto (id, title, username, content, date); // 5. 값을 가져오면 MemoResponseDto로 하나로 만들어 지는 것. 이걸로 돌면서 MemoResponseDto의 객체가 만들어진다
            }
        });
    }

    // 게시글 수정
    @PutMapping("/post/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        // 요청된 게시글 ID로 해당 게시글을 데이터베이스에서 조회합니다.
        Post post = findById(id);
        if (post != null) {
            // 게시글의 비밀번호 확인
//            System.out.println(post.getPassword());
//            System.out.println(requestDto.getPassword());
            if (Objects.equals(post.getPassword(), requestDto.getPassword())) {
                // 비밀번호가 일치하면 게시글 내용 수정
                String sql = "UPDATE post SET title = ?, username = ?, content = ? WHERE id = ?";
                jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getUsername(), requestDto.getContent(), id);

                // 수정된 게시글 정보를 다시 조회하여 반환
                Post updatedPost = findById(id);
                return updatedPost.toResponseDto();
            } else {
                // 비밀번호가 일치하지 않을 때 예외 처리
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            // 선택한 게시글이 존재하지 않을 때 예외 처리
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
    }


    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    public Long deletePost(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        String password = (String) requestBody.get("password"); //password string으로 받음
        Post post = findById(id);
        if(post != null) {
            String postPassword = post.getPassword();
            if (post.getPassword() != null && postPassword.equals(password)){
                String sql = "DELETE FROM post WHERE id = ?";
                jdbcTemplate.update(sql, id); // 2. update 사용해서 ?자리에 넣어주면 된다. (위의 내용 참고)

                return id;
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
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
                Post post = new Post(); // post 타입으로 들어간다 (이건 사용자에 맞게 변경하면 될것 같다)
                post.setTitle (resultSet.getString("title"));
                post.setUsername (resultSet.getString("username"));
                post.setPassword (resultSet.getString("password"));
                post.setContent (resultSet.getString("content"));
                post.setDate (resultSet.getDate("date").toLocalDate());
                return post;
            } else {
                throw new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id);
            }
        }, id);
    }
}
