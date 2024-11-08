public class Employee {
    private String FIO;
    private String appointment;
    private String object_id;
    private String filial_name;

    // Конструкторы, геттеры и сеттеры
    public Employee(String FIO, String appointment, String object_id, String filial_name) {
        this.FIO = FIO;
        this.appointment = appointment;
        this.object_id = object_id;
        this.filial_name = filial_name;
    }

    public String getFIO() {
        return FIO;
    }

    public String getAppointment() {
        return appointment;
    }

    public String getObject_id() {
        return object_id;
    }

    public String getFilial_name() {
        return filial_name;
    }
}
