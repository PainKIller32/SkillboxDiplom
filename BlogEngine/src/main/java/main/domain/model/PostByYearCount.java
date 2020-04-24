package main.domain.model;


import java.time.LocalDateTime;

public interface PostByYearCount {
    LocalDateTime getTime();
    int getCount();
}