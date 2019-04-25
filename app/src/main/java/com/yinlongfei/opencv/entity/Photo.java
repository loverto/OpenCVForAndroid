package com.yinlongfei.opencv.entity;

public class Photo {

    private int id;
    private String createdBy;
    private String lastModifiedBy;
    //创建日期
    private String createdDate;
    // 最后一次修改日期
    private String lastModifiedDate;
    // 用户名
    private String name;
    //用户路径
    private String originUrl;
    // 特征图路径
    private String featureUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (id != photo.id) return false;
        if (!createdBy.equals(photo.createdBy)) return false;
        if (!lastModifiedBy.equals(photo.lastModifiedBy)) return false;
        if (!createdDate.equals(photo.createdDate)) return false;
        if (!lastModifiedDate.equals(photo.lastModifiedDate)) return false;
        if (!name.equals(photo.name)) return false;
        if (!originUrl.equals(photo.originUrl)) return false;
        return featureUrl.equals(photo.featureUrl);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + lastModifiedBy.hashCode();
        result = 31 * result + createdDate.hashCode();
        result = 31 * result + lastModifiedDate.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + originUrl.hashCode();
        result = 31 * result + featureUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", createdBy='" + createdBy + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", name='" + name + '\'' +
                ", originUrl='" + originUrl + '\'' +
                ", featureUrl='" + featureUrl + '\'' +
                '}';
    }
}
