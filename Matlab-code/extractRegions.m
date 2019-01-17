function extractRegions(round, batch)

fileName = strcat('regionlist_round', num2str(round), '_', batch, '.mat')
load (fileName)

n = size(newLcell, 2);

for i = 1: n

    misclassified_regions = cell(5, 1);
    regions = cell(5, 1);  %includes all the regions
    region_counter = 1;
    misclassifiedregion_counter = 1;


    for j = 1: size(regionlistcell{1, i}, 1)
        regionIndex = regionlistcell{1, i}(j, 1);
        predLabel = regionlistcell{1, i}(j, 2);
        actualLabel = regionlistcell{1, i}(j, 3);
        confidence = regionlistcell{1, i}(j, 4);
        
        
        I = (newLcell{1, i} == regionIndex);
        I = I * 255;
        BW = im2bw(I, graythresh(I));  
        [B, M] = bwboundaries(BW);
        s = find (newLcell{1, i} == regionIndex);
        
        
        regions{1, region_counter} = B;
        regions{2, region_counter} = double(predLabel);
        regions{3, region_counter} = double(actualLabel);
        regions{4, region_counter} = double(region_counter);
        % relative elevation
        regions{5, region_counter} = 0.0 %double (elevation_value);
        region_counter = region_counter + 1;

        if (predLabel ~= actualLabel)
            misclassified_regions(:, misclassifiedregion_counter) = regions(:, region_counter - 1);
            misclassifiedregion_counter =  misclassifiedregion_counter + 1; 
       end
        
    end
    misclassified_file_name = strcat('misclassifiedRegions', '_', num2str(i), '.mat');
    region_file_name = strcat('classifiedRegions', '_', num2str(i), '.mat');
    
    folder = strcat('extractedRegions/', batch);
    cd (folder)
    save  (misclassified_file_name, 'misclassified_regions')
    save  (region_file_name, 'regions')
    cd ..
    cd ..
end

end