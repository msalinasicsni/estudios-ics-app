package ni.org.ics.estudios.appmovil.domain;



/**
 * Created by FIRSTICT on 4/28/2017.
 * V1.0
 */
public class Tamizaje extends BaseMetaData {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String codigo;
    private Participante participante;
    private char aceptaTamizaje;
    private String razonNoParticipa;
    private char areaCovertura;
    private char ninoMenor12Anios;
    private char intencionPermanecerArea;
    private char tieneTarjetaVacunaOIdentificacion;
    private char enfermedadAgudaCronica;
    private char elegible;
    private char aceptaSeroprevalenciaZik;
    private String dondeAsisteProblemasSalud;
    private char asisteCSSF;
    private String otroCentroSalud;
    private String puestoSalud;
    private char siEnfermaSoloAsistirCSSF;
    private char tomaPuntoGPSCasa;
    private String razonNoGeoreferenciacion;
    private String otraRazonNoGeoreferenciacion;


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public char getAceptaTamizaje() {
        return aceptaTamizaje;
    }

    public void setAceptaTamizaje(char aceptaTamizaje) {
        this.aceptaTamizaje = aceptaTamizaje;
    }

    public String getRazonNoParticipa() {
        return razonNoParticipa;
    }

    public void setRazonNoParticipa(String razonNoParticipa) {
        this.razonNoParticipa = razonNoParticipa;
    }

    public char getAreaCovertura() {
        return areaCovertura;
    }

    public void setAreaCovertura(char areaCovertura) {
        this.areaCovertura = areaCovertura;
    }

    public char getNinoMenor12Anios() {
        return ninoMenor12Anios;
    }

    public void setNinoMenor12Anios(char ninoMenor12Anios) {
        this.ninoMenor12Anios = ninoMenor12Anios;
    }

    public char getIntencionPermanecerArea() {
        return intencionPermanecerArea;
    }

    public void setIntencionPermanecerArea(char intencionPermanecerArea) {
        this.intencionPermanecerArea = intencionPermanecerArea;
    }

    public char getTieneTarjetaVacunaOIdentificacion() {
        return tieneTarjetaVacunaOIdentificacion;
    }

    public void setTieneTarjetaVacunaOIdentificacion(char tieneTarjetaVacunaOIdentificacion) {
        this.tieneTarjetaVacunaOIdentificacion = tieneTarjetaVacunaOIdentificacion;
    }

    public char getEnfermedadAgudaCronica() {
        return enfermedadAgudaCronica;
    }

    public void setEnfermedadAgudaCronica(char enfermedadAgudaCronica) {
        this.enfermedadAgudaCronica = enfermedadAgudaCronica;
    }

    public char getElegible() {
        return elegible;
    }

    public void setElegible(char elegible) {
        this.elegible = elegible;
    }

    public char getAceptaSeroprevalenciaZik() {
        return aceptaSeroprevalenciaZik;
    }

    public void setAceptaSeroprevalenciaZik(char aceptaSeroprevalenciaZik) {
        this.aceptaSeroprevalenciaZik = aceptaSeroprevalenciaZik;
    }

    public String getDondeAsisteProblemasSalud() {
        return dondeAsisteProblemasSalud;
    }

    public void setDondeAsisteProblemasSalud(String dondeAsisteProblemasSalud) {
        this.dondeAsisteProblemasSalud = dondeAsisteProblemasSalud;
    }

    public char getAsisteCSSF() {
        return asisteCSSF;
    }

    public void setAsisteCSSF(char asisteCSSF) {
        this.asisteCSSF = asisteCSSF;
    }

    public String getOtroCentroSalud() {
        return otroCentroSalud;
    }

    public void setOtroCentroSalud(String otroCentroSalud) {
        this.otroCentroSalud = otroCentroSalud;
    }

    public String getPuestoSalud() {
        return puestoSalud;
    }

    public void setPuestoSalud(String puestoSalud) {
        this.puestoSalud = puestoSalud;
    }

    public char getSiEnfermaSoloAsistirCSSF() {
        return siEnfermaSoloAsistirCSSF;
    }

    public void setSiEnfermaSoloAsistirCSSF(char siEnfermaSoloAsistirCSSF) {
        this.siEnfermaSoloAsistirCSSF = siEnfermaSoloAsistirCSSF;
    }

    public char getTomaPuntoGPSCasa() {
        return tomaPuntoGPSCasa;
    }

    public void setTomaPuntoGPSCasa(char tomaPuntoGPSCasa) {
        this.tomaPuntoGPSCasa = tomaPuntoGPSCasa;
    }

    public String getRazonNoGeoreferenciacion() {
        return razonNoGeoreferenciacion;
    }

    public void setRazonNoGeoreferenciacion(String razonNoGeoreferenciacion) {
        this.razonNoGeoreferenciacion = razonNoGeoreferenciacion;
    }

    public String getOtraRazonNoGeoreferenciacion() {
        return otraRazonNoGeoreferenciacion;
    }

    public void setOtraRazonNoGeoreferenciacion(String otraRazonNoGeoreferenciacion) {
        this.otraRazonNoGeoreferenciacion = otraRazonNoGeoreferenciacion;
    }

    @Override
    public String toString() {
        return "'" + codigo + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tamizaje)) return false;

        Tamizaje tamizaje = (Tamizaje) o;

        return (!codigo.equals(tamizaje.codigo));
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}