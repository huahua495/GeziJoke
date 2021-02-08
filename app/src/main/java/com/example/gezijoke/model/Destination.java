package com.example.gezijoke.model;


public class Destination {


    /**
     * isFragment : true
     * asStarter : false
     * needLogin : false
     * clazName : com.example.gezijoke.ui.dashboard.DashboardFragment
     * id : 430793601
     * pageurl : main/tabs/dashboard
     */

    private boolean isFragment;
    private boolean asStarter;
    private boolean needLogin;
    private String clazName;
    private int id;
    private String pageurl;

    public boolean isIsFragment() {
        return isFragment;
    }

    public void setIsFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }

    public boolean isAsStarter() {
        return asStarter;
    }

    public void setAsStarter(boolean asStarter) {
        this.asStarter = asStarter;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getClazName() {
        return clazName;
    }

    public void setClazName(String clazName) {
        this.clazName = clazName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPageurl() {
        return pageurl;
    }

    public void setPageurl(String pageurl) {
        this.pageurl = pageurl;
    }
}
