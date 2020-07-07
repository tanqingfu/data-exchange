package com.sdy.dataexchange.gen.s;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzq on 2018/5/28.
 */
@Slf4j
public class S {

    public void generateByTables(GenConfig genConfig, String... tableNames) {

//        String module = genConfig.getModule();
        // 数据库连接
        String dbUrl = genConfig.getDbUrl();
        String dbName = genConfig.getDbName();
        String dbPwd = genConfig.getDbPwd();
        // Author
        String author = genConfig.getAuthor();
        // Package
        String projectName = genConfig.getProjectName();
        String packageName = genConfig.getPackageName();
        String pkg = "biz";
        String dir = genConfig.getIde().equals("idea") ? "./" : "../";

        AutoGenerator autoGenerator =
                new AutoGenerator().setGlobalConfig(
                        new GlobalConfig()
                                .setActiveRecord(false)
                                .setAuthor(author)
                                .setOutputDir("c:\\codeGen")
                                .setFileOverride(false)
                                .setBaseColumnList(false) // 生成返回列字段
                                .setBaseResultMap(false) // 生成字段映射
                                .setEnableCache(false) // XML 二级缓存
                                .setServiceName("%sService")
                                .setServiceImplName("%sServiceImpl")
                                .setOpen(false)
                                .setDateType(DateType.ONLY_DATE)
                ).setDataSource(
                        new DataSourceConfig()
                                .setDbType(genConfig.getDbType())
                                .setUrl(dbUrl)
                                .setUsername(dbName)
                                .setPassword(dbPwd)
                                .setDriverName(genConfig.getDriverName())
                ).setStrategy(
                        new StrategyConfig()
                                .setCapitalMode(true)
                                .setNaming(NamingStrategy.underline_to_camel)
                                .setSuperEntityClass("com.sdy.common.model.BaseModel")
                                .setSuperMapperClass("com.sdy.mvc.mapper.BaseMapper")
                                .setSuperControllerClass("com.sdy.mvc.controller.BaseController")
                                .setSuperServiceClass("com.sdy.mvc.service.BaseService")
                                .setSuperServiceImplClass("com.sdy.mvc.service.impl.BaseServiceImpl")
                                .setInclude(tableNames)
                                .setEntityLombokModel(true)
                                .setRestControllerStyle(genConfig.getRestStyle())
                ).setPackageInfo(
                        new PackageConfig()
                                .setParent(packageName)
                                .setController("web.controller")
                                .setMapper(pkg + ".mapper")
                                .setEntity(pkg + ".model")
                                .setService(pkg + ".service")
                                .setServiceImpl(pkg + ".service.impl")
                ).setTemplateEngine(
                        new FreemarkerTemplateEngine()
                );
        autoGenerator.setTemplate(
                new TemplateConfig().setXml(null)
                        .setController("/templates/controller.java")
                        .setEntity("/templates/entity.java")
                        .setMapper("/templates/mapper.java")
                        .setXml("/templates/mapper.xml")
                        .setService("/templates/service.java")
                        .setServiceImpl("/templates/serviceImpl.java")
        ).setCfg(
                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                new InjectionConfig() {
                    @Override
                    public void initMap() {
                        Map<String, Object> map = new HashMap<>();
                        map.put("PackageName", packageName);
                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
                        this.setMap(map);
                    }
                }.setFileOutConfigList(Arrays.asList(
                        new FileOutConfig("/templates/entity.java.ftl") {
                            // 自定义输出文件目录
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return dir + projectName + "-" + pkg + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/" + pkg + "/model/" + tableInfo.getEntityName() + ".java";
                            }
                        },
                        new FileOutConfig("/templates/mapper.java.ftl") {
                            // 自定义输出文件目录
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return dir + projectName + "-" + pkg + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/" + pkg + "/mapper/" + tableInfo.getMapperName() + ".java";
                            }
                        },
                        new FileOutConfig("/templates/mapper.xml.ftl") {
                            // 自定义输出文件目录
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return dir + projectName + "-" + pkg + "/src/main/resources/mapper/" + tableInfo.getMapperName() + ".xml";
                            }
                        },
                        new FileOutConfig(genConfig.getRestStyle() ? "/templates/controller.java.ftl" : "/templates/controller.static.java.ftl") {
                            // 自定义输出文件目录
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return dir + projectName + "-web/src/main/java/" + packageName.replaceAll("\\.", "/") + "/web/controller/" + tableInfo.getControllerName() + ".java";
                            }
                        },
                        new FileOutConfig("/templates/service.java.ftl") {
                            // 自定义输出文件目录
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return dir + projectName + "-" + pkg + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/" + pkg + "/service/" + tableInfo.getServiceName() + ".java";
                            }
                        },
                        new FileOutConfig("/templates/serviceImpl.java.ftl") {
                            // 自定义输出文件目录
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return dir + projectName + "-" + pkg + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/" + pkg + "/service/impl/" + tableInfo.getServiceImplName() + ".java";
                            }
                        }
                        )
                )
        ).execute();

        if (!genConfig.getRestStyle()) {
            autoGenerator.setTemplate(
                    new TemplateConfig().setXml("/templates/page.ftl.ftl")
            ).setCfg(
                    // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                    new InjectionConfig() {
                        @Override
                        public void initMap() {
                            Map<String, Object> map = new HashMap<>();
                            map.put("PackageName", packageName);
                            map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
                            this.setMap(map);
                        }
                    }.setFileOutConfigList(Arrays.asList(
                            new FileOutConfig("/templates/page.ftl.ftl") {
                                // 自定义输出文件目录
                                @Override
                                public String outputFile(TableInfo tableInfo) {
                                    return dir + projectName + "-web/src/main/resources/webapp/public/template/page/" + tableInfo.getName() + "/page.ftl";
                                }
                            }
                            )
                    )
            ).execute();

            autoGenerator.setTemplate(
                    new TemplateConfig().setXml("/templates/new.ftl.ftl")
            ).setCfg(
                    // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                    new InjectionConfig() {
                        @Override
                        public void initMap() {
                            Map<String, Object> map = new HashMap<>();
                            map.put("PackageName", packageName);
                            map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
                            this.setMap(map);
                        }
                    }.setFileOutConfigList(Arrays.asList(
                            new FileOutConfig("/templates/new.ftl.ftl") {
                                // 自定义输出文件目录
                                @Override
                                public String outputFile(TableInfo tableInfo) {
                                    return dir + projectName + "-web/src/main/resources/webapp/public/template/page/" + tableInfo.getName() + "/new.ftl";
                                }
                            }
                            )
                    )
            ).execute();

            autoGenerator.setTemplate(
                    new TemplateConfig().setXml("/templates/update.ftl.ftl")
            ).setCfg(
                    // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                    new InjectionConfig() {
                        @Override
                        public void initMap() {
                            Map<String, Object> map = new HashMap<>();
                            map.put("PackageName", packageName);
                            map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
                            this.setMap(map);
                        }
                    }.setFileOutConfigList(Arrays.asList(
                            new FileOutConfig("/templates/update.ftl.ftl") {
                                // 自定义输出文件目录
                                @Override
                                public String outputFile(TableInfo tableInfo) {
                                    return dir + projectName + "-web/src/main/resources/webapp/public/template/page/" + tableInfo.getName() + "/update.ftl";
                                }
                            }
                            )
                    )
            ).execute();


            autoGenerator.setTemplate(
                    new TemplateConfig().setXml("/templates/detail.ftl.ftl")
            ).setCfg(
                    // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                    new InjectionConfig() {
                        @Override
                        public void initMap() {
                            Map<String, Object> map = new HashMap<>();
                            map.put("PackageName", packageName);
                            map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
                            this.setMap(map);
                        }
                    }.setFileOutConfigList(Arrays.asList(
                            new FileOutConfig("/templates/detail.ftl.ftl") {
                                // 自定义输出文件目录
                                @Override
                                public String outputFile(TableInfo tableInfo) {
                                    return dir + projectName + "-web/src/main/resources/webapp/public/template/page/" + tableInfo.getName() + "/detail.ftl";
                                }
                            }
                            )
                    )
            ).execute();
        }
        
//        autoGenerator.setTemplate(
//                new TemplateConfig().setXml("/templates/page.vue.ftl")
//        ).setCfg(
//                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
//                new InjectionConfig() {
//                    @Override
//                    public void initMap() {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("PackageName", packageName);
//                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
//                        this.setMap(map);
//                    }
//                }.setFileOutConfigList(Arrays.asList(
//                        new FileOutConfig("/templates/page.vue.ftl") {
//                            // 自定义输出文件目录
//                            @Override
//                            public String outputFile(TableInfo tableInfo) {
//                                return genConfig.getVuePath() + "/src/page/" + tableInfo.getName() + "/" + tableInfo.getEntityName() + "Page.vue";
//                            }
//                        }
//                        )
//                )
//        ).execute();
//
//        autoGenerator.setTemplate(
//                new TemplateConfig().setXml("/templates/api.js.ftl")
//        ).setCfg(
//                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
//                new InjectionConfig() {
//                    @Override
//                    public void initMap() {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("PackageName", packageName);
//                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
//                        this.setMap(map);
//                    }
//                }.setFileOutConfigList(Arrays.asList(
//                        new FileOutConfig("/templates/api.js.ftl") {
//                            // 自定义输出文件目录
//                            @Override
//                            public String outputFile(TableInfo tableInfo) {
//                                return genConfig.getVuePath() + "/src/api/" + tableInfo.getName() + "_api.js";
//                            }
//                        }
//                        )
//                )
//        ).execute();

//        autoGenerator.setTemplate(
//                new TemplateConfig().setXml("/templates/store.module.js.ftl")
//        ).setCfg(
//                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
//                new InjectionConfig() {
//                    @Override
//                    public void initMap() {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("PackageName", packageName);
//                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
//                        this.setMap(map);
//                    }
//                }.setFileOutConfigList(Arrays.asList(
//                        new FileOutConfig("/templates/store.module.js.ftl") {
//                            // 自定义输出文件目录
//                            @Override
//                            public String outputFile(TableInfo tableInfo) {
//                                return genConfig.getVuePath() + "/src/store/modules/" + tableInfo.getName() + ".js";
//                            }
//                        }
//                        )
//                )
//        ).execute();

//        autoGenerator.setTemplate(
//                new TemplateConfig().setXml("/templates/add.vue.ftl")
//        ).setCfg(
//                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
//                new InjectionConfig() {
//                    @Override
//                    public void initMap() {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("PackageName", packageName);
//                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
//                        this.setMap(map);
//                    }
//                }.setFileOutConfigList(Arrays.asList(
//                        new FileOutConfig("/templates/add.vue.ftl") {
//                            // 自定义输出文件目录
//                            @Override
//                            public String outputFile(TableInfo tableInfo) {
//                                return genConfig.getVuePath() + "/src/page/" + tableInfo.getName() + "/" + tableInfo.getEntityName() + "Add.vue";
//                            }
//                        }
//                        )
//                )
//        ).execute();
//
//        autoGenerator.setTemplate(
//                new TemplateConfig().setXml("/templates/update.vue.ftl")
//        ).setCfg(
//                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
//                new InjectionConfig() {
//                    @Override
//                    public void initMap() {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("PackageName", packageName);
//                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
//                        this.setMap(map);
//                    }
//                }.setFileOutConfigList(Arrays.asList(
//                        new FileOutConfig("/templates/update.vue.ftl") {
//                            // 自定义输出文件目录
//                            @Override
//                            public String outputFile(TableInfo tableInfo) {
//                                return genConfig.getVuePath() + "/src/page/" + tableInfo.getName() + "/" + tableInfo.getEntityName() + "Update.vue";
//                            }
//                        }
//                        )
//                )
//        ).execute();
//
//        autoGenerator.setTemplate(
//                new TemplateConfig().setXml("/templates/detail.vue.ftl")
//        ).setCfg(
//                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
//                new InjectionConfig() {
//                    @Override
//                    public void initMap() {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("PackageName", packageName);
//                        map.put("seq", !genConfig.getDbType().getDb().equals(DbType.MYSQL.getDb()));
//                        this.setMap(map);
//                    }
//                }.setFileOutConfigList(Arrays.asList(
//                        new FileOutConfig("/templates/detail.vue.ftl") {
//                            // 自定义输出文件目录
//                            @Override
//                            public String outputFile(TableInfo tableInfo) {
//                                return genConfig.getVuePath() + "/src/page/" + tableInfo.getName() + "/" + tableInfo.getEntityName() + "Detail.vue";
//                            }
//                        }
//                        )
//                )
//        ).execute();

        printSql(tableNames);
    }
    
    private void printSql(String... tableNames) {
        for (String item : tableNames) {
            System.out.println();;
        }
//        String tpl = "INSERT INTO `sys_resource` (`parent_id`, `type`, `auth`, `name`, `path`, `icon`, `sort`, `record`, `top_id`, `create_time`, `update_time`) VALUES (1, 1, 2, 'MODULE管理', '/MODULE/page', NULL, 1, 0, 0, now(), NULL);\n" +
//                "INSERT INTO `sys_resource` (`parent_id`, `type`, `auth`, `name`, `path`, `icon`, `sort`, `record`, `top_id`, `create_time`, `update_time`) select id, 2, 2, 'MODULE查询', '/MODULE/pageData;/MODULE/newPage;/MODULE/updatePage', NULL, 2, 0, top_id, now(), NULL from sys_resource where path = '/MODULE/page';\n" +
//                "INSERT INTO `sys_resource` (`parent_id`, `type`, `auth`, `name`, `path`, `icon`, `sort`, `record`, `top_id`, `create_time`, `update_time`) select id, 2, 2, 'MODULE新建', '/MODULE/save', NULL, 3, 1, top_id, now(), NULL from sys_resource where path = '/MODULE/page';\n" +
//                "INSERT INTO `sys_resource` (`parent_id`, `type`, `auth`, `name`, `path`, `icon`, `sort`, `record`, `top_id`, `create_time`, `update_time`) select id, 2, 2, 'MODULE修改', '/MODULE/update', NULL, 4, 1, top_id, now(), NULL from sys_resource where path = '/MODULE/page';\n" +
//                "INSERT INTO `sys_resource` (`parent_id`, `type`, `auth`, `name`, `path`, `icon`, `sort`, `record`, `top_id`, `create_time`, `update_time`) select id, 2, 2, 'MODULE删除', '/MODULE/delete', NULL, 5, 1, top_id, now(), NULL from sys_resource where path = '/MODULE/page';\n\n";
//        StringBuilder sb = new StringBuilder();
//        for (String item : tableNames) {
//            sb.append(tpl.replaceAll("MODULE", camelCaseName(item)));
//        }
//        log.info(sb.toString());
    }
    public static String camelCaseName(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }
    public static String underscoreName(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }
    //首字母转小写
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }
}
