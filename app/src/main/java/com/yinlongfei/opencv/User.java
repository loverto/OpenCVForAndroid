package com.yinlongfei.opencv;

public class User {

    // 用户名
    private String name;
    //用户路径
    private String originUrl;
    // 特征图路径
    private String featureUrl;
    //创建日期
    private String createDate;
    // 最后一次修改日期
    private String lastModifyDate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getFeatureUrl() {
        return featureUrl;
    }

    public void setFeatureUrl(String featureUrl) {
        this.featureUrl = featureUrl;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (originUrl != null ? !originUrl.equals(user.originUrl) : user.originUrl != null)
            return false;
        if (featureUrl != null ? !featureUrl.equals(user.featureUrl) : user.featureUrl != null)
            return false;
        if (createDate != null ? !createDate.equals(user.createDate) : user.createDate != null)
            return false;
        return lastModifyDate != null ? lastModifyDate.equals(user.lastModifyDate) : user.lastModifyDate == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (originUrl != null ? originUrl.hashCode() : 0);
        result = 31 * result + (featureUrl != null ? featureUrl.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (lastModifyDate != null ? lastModifyDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", originUrl='" + originUrl + '\'' +
                ", featureUrl='" + featureUrl + '\'' +
                ", createDate='" + createDate + '\'' +
                ", lastModifyDate='" + lastModifyDate + '\'' +
                '}';
    }
}
