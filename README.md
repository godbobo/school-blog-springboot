# school-blog-springboot

这是一款基于Spring Boot的校园博客系统的后台，算是对自己大学学的知识的一次练习吧。

系统中的项目架构是自己照葫芦画瓢写出来的，有很多的不完善之处，这些在自己吸收了更多的工作经验之后已经感受到了，但项目已经成型，修改的代价是巨大的，所以在可以用的基础上，我就不再修改其架构。新手看到这个一定不要照搬，根据自己的思考吸取好的思想，大神的话只求不要嘲笑。。。

本项目基于`Spring Boot + Spring MVC + Spring Data JPA + Hibernate + MySQL5.5`,同时在文件上传方面使用的是nginx配置的文件服务器，相关配置如下，可以酌情改为自己的路径地址,改完之后记得在项目的配置文件中将上传路径改为相应的绝对路径。

```
location /upload {
	alias html\static\upload;
	allow all;
	autoindex on;
}
```

本项目采用JPA的根据实体类自动生成数据表的机制，因此在创建完数据库之后可以不用管表的事情就交给JPA就可以了。

项目根目录提供了一个sql文件用于导入测试数据，这个文件不一定每次更新代码都会更新，如果存在数据表结构过时的问题请联系我。

