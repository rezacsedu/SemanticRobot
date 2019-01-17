function generateFeedbackround(round, batch)

fileName = strcat('regionlist_round', num2str(round), '_', batch, '.mat')
load (fileName)

folder = strcat('reasoningOutputs/', batch);
cd (folder)

n = size(newLcell, 2);
for k = 1: n

    file_reasoning_output = strcat('reasoning_output_', num2str(k), '.mat') 
    load (file_reasoning_output)


    shadow_info{1, k} = ones(size(newLcell{1, k})) * -1;
    suspicious_info{1, k} = ones(size(newLcell{1, k})) * -1;
    elevation_info{1, k} = ones(size(newLcell{1, k})) * -1;

    for i = 1 : size(Non_Suspicious, 2)
        indexes = find (newLcell{1, k} == Non_Suspicious(i));
        suspicious_info{1, k}(indexes) = 0;
    end

    for i = 1 : size(Suspicious, 2)
        indexes = find (newLcell{1, k} == Suspicious(i));
        suspicious_info{1, k}(indexes) = 1;
    end

    for i = 1 : size(regionlist, 1)
        indexes = find (newLcell{1, k} == regionlist(i, 1));
        elevation_values = elevation(indexes);
        elevation_info{1, k}(indexes) = getElevation(elevation_values);
    end
end

feedback_file_name = strcat('feedback_round_', num2str(round), '_', batch, '.mat')
save(feedback_file_name, 'shadow_info', 'suspicious_info', 'elevation_info');
end