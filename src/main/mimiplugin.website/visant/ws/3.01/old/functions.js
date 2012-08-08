function loadMethodLinks(orf, spec){
   document.visant.loadMethodLinks(orf, spec);
}
function search(orf, spec){
   //alert(spec);
   document.visant.search(orf, spec);
}
function start() {
        //alert("hi");
        document.visant.showme();
}
function openKGML(fname){
        //document.visant.clear();
        document.visant.openFile('http://mimiplugin.ncibi.org/visant/ws/3.01/sample/kegg/'+fname);
        //document.visant.openFile('http://visant.bu.edu:8080/test/DAI?command=time_proxy&target=http://www.genome.jp/kegg/KGML/KGML_v0.6.1/'+fname.substring(4));
}
function openFile(fname){
   //document.visant.clear();
    document.visant.openFile(fname);
   //return true;
}

