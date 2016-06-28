package com.example.foda_.instabugdemo;

/**
 * Created by foda on 2016-06-25.
 */
// class that will store all data that i need from json
public class DataFromJson {
    String repo_name;
    String repo_description;
    String repo_username;
    boolean fork;
    String repo_url;
    String user_url;
    public DataFromJson(String repo_name,String repo_description,String repo_username,boolean fork,
                        String repo_url,String user_url)
    {
        this.repo_name=repo_name;
        this.repo_description=repo_description;
        this.repo_username=repo_username;
        this.fork=fork;
        this.repo_url=repo_url;
        this.user_url=user_url;
    }

}
