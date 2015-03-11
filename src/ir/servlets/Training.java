package ir.servlets;

public class Training extends JsonServlet implements AppServlet{

    private static final long serialVersionUID = 8878497194596555938L;

    @Override
    public void init() {
        System.out.println("Training servlet is called");
    }

}
