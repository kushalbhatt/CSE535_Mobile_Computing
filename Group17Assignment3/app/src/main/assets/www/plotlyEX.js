var data;

function plotgraph(output){


    data = output;
    var charts = [];
    var layout = {
      title:'',
      height: 400,
      width: 368
    };

    for (var i = 0; i<output.length; i++){

        var colorPoint = 0;
        var x = output[i][0];
        var y = output[i][1];
        var z = output[i][2];
        if(i < 4){


            colorPoint = 'rgb(128, 0, 128)'
        } else if(i < 8){
            colorPoint = 'rgb(55, 128, 191)'
        } else {
            colorPoint = 'rgb(219, 64, 82)'
        }

        var trace = {
        type: 'scatter3d',
                          mode: 'lines',
                          x: x,
                          y: y,
                          z: z,
                          opacity: 1,
                          line: {
                            width: 6,
                            color: colorPoint,
                            reversescale: false
                          }


        };
        charts.push(trace);




    }


    Plotly.newPlot('graph', charts, layout);


}




function createNewPlot(walk, jog, run){

    var charts = [];
        var layout = {
          title:'',
          height: 400,
          width: 368
        };

        for (var i = 0; i<12; i++){

            console.log(data[i][0]);

            var colorPoint = 0;
            var x = data[i][0];
            var y = data[i][1];
            var z = data[i][2];
            if(i < 4){

                colorPoint = 'rgb(128, 0, 128)'
            } else if(i < 5){
                colorPoint = 'rgb(55, 128, 191)'
            } else {
                colorPoint = 'rgb(219, 64, 82)'
            }

            var trace = {
            type: 'scatter3d',
                              mode: 'lines',
                              x: x,
                              y: y,
                              z: z,
                              opacity: 1,
                              line: {
                                width: 6,
                                color: colorPoint,
                                reversescale: false
                              }


            };

            if( walk && i< 4)
            {
               charts.push(trace);
            }
            if( jog && i<8 && i >=4){
                charts.push(trace);
            }
            if( run && i<12 && i >=8){
                charts.push(trace);
            }





    }
    Plotly.newPlot('graph', charts, layout);

}



function getCheckedStatus(){

    document.getElementById("graph").innerHTML = "";
    var runCheck = document.getElementById('runPlot');
    var walkCheck = document.getElementById('walkPlot');
     var jogCheck = document.getElementById('jogPlot');
    console.log(walkCheck.checked+ " "+ jogCheck.checked+ " "+ runCheck.checked);

    createNewPlot(walkCheck.checked, jogCheck.checked, runCheck.checked )


}

