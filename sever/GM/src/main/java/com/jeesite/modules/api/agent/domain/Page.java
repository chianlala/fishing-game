package com.jeesite.modules.api.agent.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页实体
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 5631623557625630615L;

    // bootstrap-table渲染数据需要的参数
    private Integer total; // 数据总条数
    private List<T> rows; // 列表数据
    // --------

    private Map<String, Object> otherData; // 额外附加数据

    private Boolean success = true;
    private String errMsg;

    public void addOtherData(String key, Object value) {
        if (this.otherData == null) {
            this.otherData = new HashMap<>();
        }
        if (key == null || key.equals("")) {
            return;
        }
        this.otherData.put(key, value);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Map<String, Object> getOtherData() {
        return otherData;
    }

    public void setOtherData(Map<String, Object> otherData) {
        this.otherData = otherData;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
