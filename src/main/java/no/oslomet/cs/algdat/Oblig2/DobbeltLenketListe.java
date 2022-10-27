package no.oslomet.cs.algdat.Oblig2;


////////////////// class DobbeltLenketListe //////////////////////////////


import java.util.*;


public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     *
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {
        hode = null;
        hale = null;
        antall = 0;
        endringer = 0;
    }

    public DobbeltLenketListe(T[] a) {
        this.hode = null;
        this.hale = null;

        //her sjekker jeg om tabellen er tøm, og hvis ja sender feil mld
        if (a == null) {
            throw new NullPointerException("tabbelen a er null!");
        }
        if(a.length > 0){
            int i = 0;
            for (; i<a.length; i++){
                if (a[i] != null) {
                    hode = new Node<>(a[i]);
                    antall++;
                    hale = hode;
                    break;
                }
            }
            if (hale != null) {
                i++;
                for(; i<a.length; i++){
                    if (a[i] != null) {
                        hale.neste = new Node<>(a[i], hale, null);
                        hale = hale.neste;
                        antall++;
                    }
                }
            }
        }
    }

    public Liste<T> subliste(int fra, int til) {
        fratilkontroll(antall, fra, til);

        Liste<T> liste = new DobbeltLenketListe<>();
        int lengde = til - fra;
        if (lengde < 1) {
            return liste;
        }
        Node<T> hjelpeNOde = finnNode(fra);
        while (lengde > 0) {
            liste.leggInn(hjelpeNOde.verdi);
            hjelpeNOde = hjelpeNOde.neste;
            lengde--;
        }
        return liste;

    }
    //vi bruekr her en hjelpemetode
    private void fratilkontroll(int antall, int fra, int til) {
        if (fra < 0) {                                // fra er negativ
            throw new IndexOutOfBoundsException("fra(" + fra + ") er negativ!");
        }
        if (til > antall) {                        // til er utenfor tabellen
            throw new IndexOutOfBoundsException("til(" + til + ") > antall(" + antall + ")");
        }
        if (fra > til) {                               // fra er større enn til
            throw new IllegalArgumentException("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");
        }
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
        if(antall > 0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean leggInn(T verdi) {
        verdi = Objects.requireNonNull(verdi, "ikke tillat med null");

        Node<T> newnode = new Node<>(verdi);

        if (antall == 0 && hale == null && hode == null) {
            hode = newnode;
            hale = hode;
        } else {
            hale.neste = newnode;
            newnode.forrige = hale;
            hale = newnode;
        }
        endringer++;
        antall++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        Objects.requireNonNull(verdi,"Ikke tillat med null");
        //1- sjekk for negativ og indeks mindre enn antall
        if(indeks < 0 || antall < indeks){
            verdi = Objects.requireNonNull(verdi,"indeks mindre enn null, negativ og større enn antall er ikke lov");
        }
        indeksKontroll(indeks, true);

        //tom liste
        if(antall == 0) {
            leggInn(verdi);
            return;
        }

        else if(indeks == 0){
            Node<T> ny = new Node<>(verdi);
            ny.neste = hode;
            hode.forrige = ny;
            hode = ny;

        }

        else if(indeks == antall){
            Node<T> ny = new Node<>(verdi);
            ny.forrige = hale;
            hale.neste = ny;
            hale = ny;
        }

        else if(indeks > antall/2){
            Node<T> aktuell = hode;
            for(int i = 0; i < indeks-1; i++){
                aktuell = aktuell.neste;
            }

            Node<T> ny = new Node<>(verdi);
            ny.neste = aktuell.neste;
            ny.forrige = aktuell;
            aktuell.neste = ny;
            ny.neste.forrige = ny;
        }

        else{
            Node<T> aktuell = hale;
            for(int i = antall; i > indeks; i--){
                aktuell = aktuell.forrige;
            }

            Node<T> ny = new Node<>(verdi);
            ny.neste = aktuell.neste;
            ny.forrige = aktuell;
            aktuell.neste = ny;
            ny.neste.forrige = ny;


        }
        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi) {
        //koppiert en del kode fra kompednie(program kode 3,2,2 g)
        if (indeksTil(verdi) == -1){
            return false;
        }else {
            return true;
        }

    }

    @Override
    public T hent(int indeks) {
        indeksKontroll(indeks,false);
        Node<T> finnernode=finnNode(indeks);
        return finnernode.verdi;
    }

    private Node<T> finnNode(int indeks) {
        indeksKontroll(indeks,false);


        Node<T> current;
            if (indeks <= antall / 2) {
                current = hode;
                int i=0;
               while(i<indeks){
                   current = current.neste;
                   i++;
                }
            }else{
                    current = hale;
                int i=antall-1;
                    while(i>indeks){
                        current =current.forrige;
                        i--;
                    }
            }
        return current;
        }


    @Override
    public int indeksTil(T verdi) {//har fått tips via denne netsiden (https://www.cs.hioa.no/~ulfu/appolonius/kildekode/TabellListe.html)
        if (antall <= 0 || verdi == null) {
            return -1;
        }
        Node<T> p = hode;
        for (int i = 0; i < antall; i++) {
            if (p.verdi.equals(verdi)) {
                return i;
            }
            p = p.neste;
        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        if(nyverdi == null ) throw new NullPointerException("Ikke tillatt med null-verdier!");
        indeksKontroll(indeks, false);  // Se Liste, false: indeks = antall er ulovlig

        Node<T> newNode = finnNode(indeks);
        T gammelVerdi = newNode.verdi;
        newNode.verdi = nyverdi;
        endringer ++;
        return gammelVerdi;
    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi == null)return false;
        Node<T> current = hode;
        int i=1;

        while (!current.verdi.equals(verdi) && i<antall){
            current=current.neste;
            i++;
        }
        if (i==antall && !current.verdi.equals(verdi)){return false;}
        if (antall==1){
            hode=null;
            hale=null;
        } else if (current.forrige==null) {
            hode.neste.forrige=null;
            hode=hode.neste;
        } else if (current.neste==null) {
            hale.forrige.neste=null;
            hale=hale.forrige;
        }
        else {
            current.forrige.neste=current.neste;
            current.neste.forrige=current.forrige;
        }
        antall--;
        endringer++;
        return true;

    }

    @Override
    public T fjern(int indeks) {
       //sjekker for indeks kaster ikke noe feil melding siden oppgaven ikke spurt om
        indeksKontroll(indeks, false);

        T current;
        if(antall == 1){ // hvis et element
            current = hode.verdi;
            hode = hale = null;
        }else if(indeks == 0){
            current = hode.verdi;
            hode.neste.forrige = null;
            hode = hode.neste;
        }else if(indeks == antall-1){
            current = hale.verdi;
            hale.forrige.neste = null;
            hale = hale.forrige;
        }else{
            Node<T> newcurrent=hode;
            for (int i=0; i<indeks; i++){
                newcurrent=newcurrent.neste;
            }
            current= newcurrent.verdi;
            newcurrent.forrige.neste=newcurrent.neste;
            newcurrent.neste.forrige=newcurrent.forrige;
        }
        antall--;
        endringer++;
        return current;
    }

    @Override
    public void nullstill() {
        Node<T> node = hode.neste;
        while(node != null){
            node.verdi = null;
            node.forrige.neste = null;
            node = node.neste;
        }
        hode = hale = null;
        antall = 0;
        endringer++;

    }

    @Override
    public String toString() {
        StringBuilder liste = new StringBuilder();
        liste.append("[");
        if (!tom()) {
            Node<T> current = hode;
            while (current != null) {
                if (current == hale) {
                    liste.append(current.verdi);
                } else {
                    liste.append(current.verdi);
                    liste.append(", ");
                }
                current = current.neste;
            }
        }
            liste.append("]");
            return liste.toString();
        }


    public String omvendtString() {
        StringBuilder liste = new StringBuilder();
        liste.append("[");
        Node<T> current = hale;
        /*
        while (current == null || antall == 0){
            System.out.println(liste.append("]"));
            break;
        }

         */
        while(current != null) {
            if (current == hode) {
                liste.append(current.verdi);
            } else {
                liste.append(current.verdi);
                liste.append(", ");
            }
            current = current.forrige;
        }
        liste.append("]");
        return liste.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
        indeksKontroll(indeks,false);
        return new DobbeltLenketListeIterator(indeks);
    }

    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator() {
            denne = hode;     // p starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks) {
            denne=finnNode(indeks);
            fjernOK=false;
            iteratorendringer=endringer;
        }

        @Override
        public boolean hasNext() {
            return denne != null;
        }

        @Override
        public T next() {
            if (iteratorendringer!=endringer){
                throw new ConcurrentModificationException("itrator og endringer er ikke like");
            }
            if (hasNext()!=true){
                throw new NoSuchElementException("listen er tøm");
            }
            T verdi= denne.verdi;
            denne=denne.neste;
            fjernOK=true;
            return verdi;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }

} // class DobbeltLenketListe


