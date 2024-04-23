-- Cargamos el CSV en una relación con cada columna etiquetada
data = LOAD '[vuestro_nombre_apellidos]/input/data.csv' USING PigStorage(',') AS (
    order_id:chararray,
    order_date:chararray,
    total_amount:float,
    order_item_id:chararray,
    quantity:int,
    unit_price:float,
    customer_id:chararray,
    username:chararray,
    email:chararray,
    age:int,
    country:chararray
);

-- Agrupamos los datos por país y sumamos el total gastado por cada usuario
grouped = GROUP data BY (customer_id, country);
sum_spent = FOREACH grouped GENERATE FLATTEN(group), SUM(data.total_amount) as total_spent;

-- Ordenamos los resultados para obtener el máximo gastador por país
ordered = ORDER sum_spent BY (country, total_spent DESC);
max_spent_by_country = FOREACH (GROUP ordered BY country) GENERATE FLATTEN(group) AS country, MAX(ordered.total_spent);

-- Almacenamos el resultado
STORE max_spent_by_country INTO '[vuestro_nombre_apellidos]/output/max_spent_by_country' USING PigStorage(',');



pig -x mapreduce max_spent_by_country.pig