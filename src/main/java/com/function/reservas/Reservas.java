package com.function.reservas;

public class Reservas {
    private String id;
    private String clienteid;
    private String mesaid;
    private Integer capacidade;
    private String data;
    
    // Getters e setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClienteid() { return clienteid; }
    public void setClienteid(String clienteid) { this.clienteid = clienteid; }

    public String getMesaid() { return mesaid; }
    public void setMesaid(String mesaid) { this.mesaid = mesaid; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Reservas(String id, String clienteid, String mesaid, Integer capacidade, String data) {
        this.id = id;
        this.clienteid = clienteid;
        this.mesaid = mesaid;
        this.capacidade = capacidade;
        this.data = data;
    }
}
