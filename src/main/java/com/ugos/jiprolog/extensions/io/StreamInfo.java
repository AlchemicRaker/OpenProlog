package com.ugos.jiprolog.extensions.io;

import java.util.Properties;


public class StreamInfo {
    private String name;
    private int handle;
    protected Properties properties = new Properties();
    static int MAX_VALUE = Integer.MAX_VALUE - 1;
    private boolean binary;
//	public StreamInfo(String name)
//	{
//		this(name, "#" + name.hashCode());
//	}

    public StreamInfo(String name, int handle) {
        this.name = name;
        this.handle = handle;

        properties.setProperty("file_name", String.format("file_name('%s')", name.replace("\\", "/")));
        properties.setProperty("alias", String.format("alias('%d')", handle));
        properties.setProperty("type", "type(text)");
        properties.setProperty("end_of_stream", "end_of_stream(not)");

        setBinary(false);
    }

    public String getName() {
        return name;
    }


    public String getAlias() {
        String alias = properties.getProperty("alias");
        int pos = alias.indexOf('(');
        return alias.substring(pos + 1, alias.lastIndexOf(')'));
    }

    public void setAlias(String alias) {
        properties.setProperty("alias", String.format("alias(%s)", alias));
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEndOfStream(String pos) {
        properties.setProperty("end_of_stream", String.format("end_of_stream(%s)", pos));
    }

    public String getEndOfStream() {
        return properties.getProperty("end_of_stream", "no");
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

}

