<?PHP
$hostname_localhost ="localhost";
$database_localhost ="tracker_system_db";
$username_localhost ="root";
$password_localhost ="";
$json = array();
		if(isset($_GET["user"])){
			
			$vehicle = $_GET["vehicle"];
			$cedula = $_GET["cedula"];
			$destino = $_GET["destino"];
			$justificacion = $_GET["justificacion"];
			$fechaInicio = $_GET["dateBegin"];
			$horaInicio = $_GET["beginHour"];
			$fechaFinal = $_GET["dateEnd"];
			$horaFinal = $_GET["endHour"];
			
			$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
			
			$consulta="select FK_Vehicle,FK_User,Details,Justification,PK_Date,beginHour,endHour from t_routes where FK_Vehicle='{$vehicle}' and (PK_Date = '{$fechaInicio}' and PK_Date = '{$fechaFinal}') and ((beginHour <= '{$horaInicio}' and endHour >= '{$horaFinal}') or (endHour >= '{$horaInicio}' and endHour <= '{$horaFinal}'))";
			
			$resultado=mysqli_query($conexion,$consulta);
			date_default_timezone_set('UTC');
			
			
			if(mysqli_num_rows($resultado)==0){
				$consulta2="insert into t_routes(PK_Date,Justification,Travel,Details,FK_User,FK_Vehicle,beginHour,endHour) values('{$fechaInicio}','{$justificacion}','','{$destino}','{$cedula}','{$vehicle}','{$horaInicio}','{$horaFinal}')";
				$resultado2=mysqli_query($conexion,$consulta2);
				if(resultado2 == true){
					$json = [["Resultado" => "1"]];
					echo json_encode($json);
				}
				
			}else{
				if($registro=mysqli_fetch_array($resultado)){
					$json = [["Resultado" => "0"], 
					["inicio" => $registro[5]],
					["final" => $registro[6]]];
					echo json_encode($json);
				}
			}
			mysqli_close($conexion);
			
			
		}else{
			$json = [["Resultado" => "2"]];
			echo json_encode($json);
		}
	