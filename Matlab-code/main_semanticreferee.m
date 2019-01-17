%% Introduction

% This code is for training a U-Net on Matlab on the UC Merced Land Use
% data set with DLSR dense labeled images used in the work:
% Semantic Referee: A Neural-Symbolic Framework for Enhancing Geospatial
% Semantic Segmentation
% by Marjan Alirezaie, Martin Längkvist, Michael Sioutis, Amy Loutfi

% Step 1:
% Download the UC Merced Land Use Dataset from:
% http://weegee.vision.ucmerced.edu/datasets/landuse.html
% and put it in './ucmerced'
% For citation of this work:
%@inproceedings{yang2010bag,
%   title={Bag-of-visual-words and spatial extensions for land-use classification},
%   author={Yang, Yi and Newsam, Shawn},
%   booktitle={Proceedings of the 18th SIGSPATIAL international conference on advances in geographic information systems},
%   pages={270--279},
%   year={2010},
%   organization={ACM}
% }
%
% Step 2:
% Download the DLRSD data set from:
% https://sites.google.com/view/zhouwx/dataset
% and put in './ucmerced/DLRSD'
% For citation of this work:
% @article{shao2018performance,
%   title={Performance Evaluation of Single-Label and Multi-Label Remote Sensing Image Retrieval Using a Dense Labeling Dataset},
%   author={Shao, Zhenfeng and Yang, Ke and Zhou, Weixun},
%   journal={Remote Sensing},
%   volume={10},
%   number={6},
%   pages={964},
%   year={2018},
%   publisher={Multidisciplinary Digital Publishing Institute}
% }
%
% Step 3:
% Create the following folders:
% './round/train
% './round/test
% './round/val
% './labels/train
% './labels/test
% './labels/val
%
% Step 4 (optional):
% cd('...'); % Change to folder where this file is


%% Prepare data

dirsdata = dir('.\ucmerced\Images\**\*.tif');
dirslabels = dir('.\DLRSD\Images\**\*.png');

% Make data and labels
data = zeros(256, 256, 3, 2100,'uint8');
labels = zeros(256, 256, 2100,'uint8');
for i = 1:2100
    [~, dirsfilename, ~] = fileparts(dirsdata(i).name);
    [~, labelsfilename, ~] = fileparts(dirslabels(i).name);
    if strcmp(dirsfilename, labelsfilename)
        tempdata = imread(fullfile(dirsdata(i).folder, dirsdata(i).name));
        if size(tempdata,1)~=256 || size(tempdata,2)~=256
            tempdata = imresize(tempdata, [256 256]);
        end
        data(:,:,:,i) = tempdata;
        labels(:,:,i) = imread(fullfile(dirslabels(i).folder, dirslabels(i).name));
    else
        fprintf('Not match for i=%i\n', i)
    end
end

% Remove any images with tenniscourt (class 6) and field (class 8)
removeids = [find(squeeze(sum(sum(labels==6,1),2))~=0); find(squeeze(sum(sum(labels==8,1),2))~=0)];
data(:,:,:,removeids) = [];
labels(:,:,removeids) = [];
dirsdata(removeids) = [];
dirslabels(removeids) = [];

% Rename labels
% Old label  New label
labelchange = [...
    1 , 6;... % airplane
    2 , 2;... % bare soil
    3 , 4;... % buildings
    4 , 7;... % cars
    5 , 2;... % chaparral
    6 , 2;...% court
    7 , 3;... % dock
    8 , 1;... % field
    9 , 1;...% grass
    10, 4;... % mobile home
    11, 3;... % pavement
    12, 2;... % sand
    13, 5;... % sea
    14, 8;... % ship
    15, 4;... % tanks
    16, 1;... % trees
    17, 5];   % water
% New class name (old class names)
% 1 Vegatation (trees/field/grass)
% 2 Non-vegetation ground (bare soil/Sand/chaparral/court)
% 3 pavement (pavement, dock)
% 4 Building (building/mobile home/tank)
% 5 Water (water/sea)
% 6 airplane (ariplane)
% 7 cars (cars)
% 8 ship (ship)

% Split data
[traininds, valinds, testinds] = split(1:size(data,4), [0.8 0.1 0.1], 0);

% Make training, validation, test labels (run once)
for i=1:length(traininds)
    tempdata = labels(:,:,traininds(i));
    [~, dirsfilename, ~] = fileparts(dirsdata(traininds(i)).name);
    imwrite(tempdata, ['.\labels\train\' dirsfilename '.png'])
end
for i=1:length(valinds)
    tempdata = labels(:,:,valinds(i));
    [~, dirsfilename, ~] = fileparts(dirsdata(valinds(i)).name);
    imwrite(tempdata, ['.\labels\val\' dirsfilename '.png'])
end
for i=1:length(testinds)
    tempdata = labels(:,:,testinds(i));
    [~, dirsfilename, ~] = fileparts(dirsdata(testinds(i)).name);
    imwrite(tempdata, ['.\labels\test\' dirsfilename '.png'])
end


%% Make training, validation, test images

round = 1; % change here

load(['.\feedback_round_' num2str(round - 1) '_train.mat']) % feedback file from reasoner containing shadow_info, elevation_info, and suspicious_info
for i=1:length(traininds)
    tempdata = data(:,:,:,traininds(i));
    if isempty(shadow_info{i})  || round == 1
        tempdata = cat(3,tempdata, zeros(256,256,3)); % add zeros if first round
    else
        tempdata = cat(3,tempdata, shadow_info{i}, elevation_info{i}, suspicious_info{i});
    end
    [~, dirsfilename, ~] = fileparts(dirsdata(traininds(i)).name);
    save(['.\round' num2str(round) '\train\' dirsfilename '.mat'],'tempdata');
end
load(['.\feedback_round_' num2str(round - 1) '_val.mat'])
for i=1:length(valinds)
    tempdata = data(:,:,:,valinds(i));
    if isempty(shadow_info{i})  || round == 1
        tempdata = cat(3,tempdata, zeros(256,256,3));
    else
        tempdata = cat(3,tempdata, shadow_info{i}, elevation_info{i}, suspicious_info{i});
    end
    [~, dirsfilename, ~] = fileparts(dirsdata(valinds(i)).name);
    save(['.\round' num2str(round) '\val\' dirsfilename '.mat'],'tempdata');
end
load(['.\feedback_round_' num2str(round - 1) '_test.mat'])
for i=1:length(testinds)
    tempdata = data(:,:,:,testinds(i));
    if isempty(shadow_info{i}) || round == 1
        tempdata = cat(3,tempdata, zeros(256,256,3));
    else
        tempdata = cat(3,tempdata, shadow_info{i}, elevation_info{i}, suspicious_info{i});
    end
    [~, dirsfilename, ~] = fileparts(dirsdata(testinds(i)).name);
    save(['.\round' num2str(round) '\test\' dirsfilename '.mat'],'tempdata');
end

%% Make segmentation

numRegions = 1000;
segdata = zeros(256,256,size(data,4),'single');
for i=1:size(data,4)
    tempdata = data(:,:,:,i);
    [segdata(:,:,i), numlabels] = superpixels(tempdata, numRegions); % requires matlab R2016a or later
end

save('.\segmentationSWJ.mat', 'segdata', 'dirsdata')


%% Train U-net

round = 1; % change here

numClasses = 8;
numChannels = 6;
minibatchsize = 16; % can change here depending on your gpu
patchsize = 256;

classNames = ["Vegatation",...
    "Ground",...
    "Pavement",...
    "Building",...
    "Water",...
    "Airplane",...
    "Cars",...
    "Ship"];

trainfile = ['.\round' num2str(round) '\train'];
valfile = ['.\round' num2str(round) '\val'];
trainlabelsfile = '.\labels\train';
vallabelsfile = '.\labels\val';

train_ds = pixelLabelImageDatastore(imageDatastore(trainfile,'FileExtensions','.mat','ReadFcn',@matReader),...
    pixelLabelDatastore(trainlabelsfile,classNames,1:numClasses));
val_ds = pixelLabelImageDatastore(imageDatastore(valfile,'FileExtensions','.mat','ReadFcn',@matReader),...
    pixelLabelDatastore(vallabelsfile,classNames,1:numClasses));

% Create the U-net
lgraph = createUnet([patchsize, patchsize, numChannels], numClasses);

% % Use class weight matrix
tbl = countEachLabel(train_ds);
totalNumberOfPixels = sum(tbl.PixelCount);
frequency = tbl.PixelCount / totalNumberOfPixels;
classWeights = 1./frequency;
pxLayer = pixelClassificationLayer('Name','pixel','ClassNames',tbl.Name,'ClassWeights',classWeights);
lgraph = removeLayers(lgraph,'Segmentation-Layer');
lgraph = addLayers(lgraph, pxLayer);
lgraph = connectLayers(lgraph,'Softmax-Layer','pixel');

% Training parameters
options = trainingOptions('adam', ...
    'ExecutionEnvironment','gpu', ...
    'InitialLearnRate', 1e-4,...
    'MaxEpochs',100000, ...
    'MiniBatchSize', minibatchsize, ...
    'Shuffle','every-epoch', ...
    'Verbose',1, ...
    'Plots','training-progress', ...
    'ValidationData', val_ds,...
    'ValidationFrequency', 50,...
    'ValidationPatience', 50);

%Train the Network
[net,info] = trainNetwork(train_ds, lgraph, options);

% Save the network
save(['.\net_round' num2str(round) '_withclassWeight'], 'net', 'info');


%% Make predictions, scores, regionlist

% Load segmentation
load('.\segmentationSWJ.mat');

% Load model
load(['.\net_round' num2str(round) '_classWeight']);

for seti = {'train', 'val', 'test'}
    
    set = seti{1};
    
    switch set
        case 'train'
            currentinds = traininds;
        case 'val'
            currentinds = valinds;
        case 'test'
            currentinds = testinds;
    end
    
    % Data and labels
    testdatafile = ['.\round' num2str(round) '\' set];
    testlabelsfile = ['.\labels\' set];
    testimds = imageDatastore(testdatafile, 'FileExtensions','.mat','ReadFcn',@matReader);
    testpxds = pixelLabelDatastore(testlabelsfile, classNames, 1:numClasses);
    
    % Classify test images
    [semIm] = semanticseg(testimds, net, 'outputtype', 'uint8', 'MiniBatchSize', minibatchsize);
    results = evaluateSemanticSegmentation(semIm, testpxds);
    
    % Show results
    results.DataSetMetrics
    results.ClassMetrics
    results.NormalizedConfusionMatrix
    
    % Make regionlistcell, newLcell
    numImages = length(testimds.Files);
    regionlistcell = cell(1,numImages);
    newLcell = cell(1,numImages);
    for iter=1:numImages
        % Load image, label
        segtrain = segdata(:,:, currentinds(iter));
        tempdata = readimage(testimds, iter);
        [segmentedImage, scoresImage, allScores] = semanticseg(tempdata, net, 'outputtype', 'uint8');
        templabels = uint8(readimage(testpxds, iter));
        
        % Make indexes
        numRegions = max(segtrain(:));
        segtraininds = cell(1,numRegions);
        for i = 1:numRegions
            segtraininds{i} = find(segtrain==i);
        end
        
        % Average predictions in each region
        segmentedImage_avg = zeros(size(segmentedImage));
        for i = 1:numRegions
            tempinds = segtraininds{i};
            [~, tempXl] = max(histc(segmentedImage(tempinds), 0:numClasses));
            segmentedImage_avg(tempinds) = tempXl - 1;
        end
        
        %figure; subplot(1,2,1); imagesc(segmentedImage, [1 numClasses]); subplot(1,2,2); imagesc(segmentedImage_avg, [1 numClasses])
        
        % Merge regions with same classification
        newL = zeros(size(segmentedImage_avg));
        iiter = 1;
        for r=1:size(newL,1)
            for c=1:size(newL,2)
                if newL(r,c)==0
                    temp = zeros(size(newL));
                    temp(segmentedImage_avg==segmentedImage_avg(r,c)) = 1;
                    [BW, idx] = bwselect(temp,c,r,4);
                    newL(idx) = iiter;
                    iiter = iiter + 1;
                end
            end
        end
        
        % Make regionlist
        numRegions = max(newL(:));
        regionlist = zeros(numRegions, 4);
        for i = 1:numRegions
            tempinds = find(newL==i);
            
            [~, tempXl] = max(histc(segmentedImage(tempinds), 0:numClasses));
            tempXl = tempXl - 1;
            
            [~, tempL] = max(histc(templabels(tempinds), 0:numClasses));
            tempL = tempL - 1;
            
            tempXrec = mean(scoresImage(tempinds));
            
            regionlist(i,:) = [double(i) tempXl tempL tempXrec];
        end
        
        regionlistcell{iter} = regionlist;
        newLcell{iter} = newL;
        %fprintf('%i/%i\n', iter, numImages);
        
    end
    
    save(['.\results\round' num2str(round) '\regionlist_round' num2str(round) '_' set '.mat'], 'regionlistcell', 'newLcell', 'testimds', 'testpxds')
    
end


