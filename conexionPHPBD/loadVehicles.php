<?PHP
$hostname_localhost ="localhost";
$database_localhost ="tracker_system_db";
$username_localhost ="root";
$password_localhost ="";

$json=array();

	if(isset($_GET["user"])){
		
				
		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

		$consulta="select PK_License_plate,Model,Brand from t_vehicle";
		
		$resultado = mysqli_query($conexion,$consulta);
		/*	if($registro=mysqli_fetch_row($resultado)){
				
			//$json['userData'][]="User: ".$registro[0]." Password: ".$registro[1]." Role: ".$registro[2]."}";
			
			//$json = ['plate' => $registro[0], 'model' => $registro[1], 'brand' => $registro[2]];
			$json[] = $registro;
	*/
		$arr = array();  
		if(mysqli_num_rows($resultado) != 0) {  
			while($row = mysqli_fetch_assoc($resultado)) {  
				$arr[] = $row;
			 }  
		}else{
			$resultar["user"]="";
			$resultar["password"]='no registra';
			$resultar["role"]='no registra';
			$json['usuario'][]=$resultar;
		}
		
		mysqli_close($conexion);
		//echo json_encode($json);
		echo $json_info = json_encode($arr);  
		
	}
	else{
		$json = ['plate' => "", 'model' => "", 'brand' => ""];
		echo json_encode($json);
	}
?>