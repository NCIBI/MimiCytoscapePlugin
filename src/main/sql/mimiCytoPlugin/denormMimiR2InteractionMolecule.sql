-- =============================================
--	Jing Gao; David States
-- =============================================
USE mimiR2v1
IF OBJECT_ID ( 'mimiCytoPlugin.denormMimiR2InteractionMolecule', 'P' ) IS NOT NULL 
    DROP PROCEDURE mimiCytoPlugin.denormMimiR2InteractionMolecule
GO
CREATE procedure [mimiCytoPlugin].[denormMimiR2InteractionMolecule]
	@list varchar(max),	
	@steps int,
	@taxid int,	
	@moleculeType varchar(50),
	@dataSource varchar(50),
	@condition int,
	@filter varchar(10),
	@queryType int

as

BEGIN
Declare @tempgene table  (
	symbol varchar(255),
	geneID int,   
	step int	
)

--Input is gene symbol list
Declare @StartSet table (
	symbol varchar(255)
)

--filter start gene symbol list
Declare @fltStartSet table (
	symbol varchar(255)
)

--Input is gene ID list
Declare @StartGeneIDSet table(
	geneID varchar(20)
)

Declare @nstep int



if(@queryType=1)--query MIMI by Id, set default molecule type protein->protein 
Begin
	--put input gene ids into table @StartGeneIDSet
	Insert Into @StartGeneIDSet
	Select token
	from dbo.SplitDelim(@list, ' ')


    Insert Into @tempgene(symbol, geneID, step)
	Select g.symbol, s.geneid, 0
	From @StartGeneIDSet s 
		join geneR2.dbo.gene g on s.geneID =g.geneid		

	-- Loop over steps to add neighboring genes
	set @nstep = 1
	while(@nstep <= @steps)
	begin
		insert into @tempgene(symbol, geneID,step)
		Select distinct dggi.symbol2, dggi.geneid2, @nstep
		From @tempgene tg
			join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1
				and dggi.taxid1=dggi.taxid2 --and dggi.molType1=dggi.molType2 and dggi.molType1='protein'
				and dggi.symbol2 not in (select symbol from @tempgene)		
		set @nstep = @nstep + 1
	end
	update @tempgene set step=0 where geneid in (select geneid from @StartGeneIDSet)	
End 



if (@queryType=0)--query MIMI by name or file
Begin
--put input genes into table StartSet
Insert Into @StartSet
Select token
from dbo.SplitDelim(@list, ' ')




--taxid, molecule type, data source are specified
if (@condition=1)
begin	
	    Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid

		Insert Into @fltStartSet (symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid
		
 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 and dggi.taxid2 =@taxid
					join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID = dggia.ggIntID 
						and dggia.attrType='Provenance' and dggia.attrValue=@dataSource
					join denorm.GeneAttribute dga on dggi.geneid2=dga.geneid 
						and dga.attrType='molecule type' and dga.attrValue=@moleculeType
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)
								and dggi1.taxid2=@taxid
							join denorm.GeneGeneInteractionAttribute dggia1 on dggi1.ggIntID=dggia1.ggIntID
								and dggia1.attrType='Provenance'and dggia1.attrValue=@dataSource
							join denorm.GeneAttribute dga on dggi1.geneid2=dga.geneid 
								and dga.attrType='molecule type' and dga.attrValue=@moleculeType
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1
							join denorm.GeneGeneInteractionAttribute dggia2 on dggi2.ggIntID=dggia2.ggIntID
								and dggia2.attrType='Provenance' and dggia2.attrValue=@dataSource
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)
end


--taxid and molecule type are specified
if(@condition=2)
begin	
	    Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid

		Insert Into @fltStartSet (symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid
 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 and dggi.taxid2=@taxid
					join denorm.GeneAttribute dga on dggi.geneid2=dga.geneid 
						and dga.attrType='molecule type' and dga.attrValue=@moleculeType
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)
								and dggi1.taxid2=@taxid
							join denorm.GeneAttribute dga on dggi1.geneid2=dga.geneid 
								and dga.attrType='molecule type' and dga.attrValue=@moleculeType
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)
end


--taxid and datasource id  specified
if (@condition=3)
begin				
		Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid

		Insert Into @fltStartSet (symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid			
 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 and dggi.taxid2 =@taxid
					join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID = dggia.ggIntID 
						and dggia.attrType='Provenance' and dggia.attrValue=@dataSource					
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)
								and dggi1.taxid2=@taxid
							join denorm.GeneGeneInteractionAttribute dggia1 on dggi1.ggIntID=dggia1.ggIntID
								and dggia1.attrType='Provenance'and dggia1.attrValue=@dataSource							
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1
							join denorm.GeneGeneInteractionAttribute dggia2 on dggi2.ggIntID=dggia2.ggIntID
								and dggia2.attrType='Provenance' and dggia2.attrValue=@dataSource
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)		
end


-- taxid specified
if (@condition=4)
begin				
		Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid

		Insert Into @fltStartSet (symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol and g.taxid = @taxid
			
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 and dggi.taxid2 =@taxid
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)
								and dggi1.taxid2=@taxid							
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1							
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)		
end



--molecule type and datasource specified
if (@condition=5)
	begin				
		Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol 
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid

		Insert Into @fltStartSet(symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol 
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid
 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 
					join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID = dggia.ggIntID 
						and dggia.attrType='Provenance' and dggia.attrValue=@dataSource
					join denorm.GeneAttribute dga on dggi.geneid2=dga.geneid 
						and dga.attrType='molecule type' and dga.attrValue=@moleculeType
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)								
							join denorm.GeneGeneInteractionAttribute dggia1 on dggi1.ggIntID=dggia1.ggIntID
								and dggia1.attrType='Provenance'and dggia1.attrValue=@dataSource
							join denorm.GeneAttribute dga on dggi1.geneid2=dga.geneid 
								and dga.attrType='molecule type' and dga.attrValue=@moleculeType
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1
							join denorm.GeneGeneInteractionAttribute dggia2 on dggi2.ggIntID=dggia2.ggIntID
								and dggia2.attrType='Provenance' and dggia2.attrValue=@dataSource
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)
end


-- molecule type specified
if (@condition=6)
begin				
		Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid

		Insert Into @fltStartSet(symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol
			join denorm.GeneAttribute dga on dga.attrType='molecule type' 
				 and dga.attrValue=@moleculeType and g.geneid =dga.geneid

 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 					
					join denorm.GeneAttribute dga on dggi.geneid2=dga.geneid 
						and dga.attrType='molecule type' and dga.attrValue=@moleculeType
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)						
							join denorm.GeneAttribute dga on dggi1.geneid2=dga.geneid 
								and dga.attrType='molecule type' and dga.attrValue=@moleculeType
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1							
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)		
end


-- datasource specified 
if (@condition=7)
begin				
		Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol 

		Insert Into @fltStartSet(symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol 
 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 
					join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID = dggia.ggIntID 
						and dggia.attrType='Provenance' and dggia.attrValue=@dataSource					
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)							
							join denorm.GeneGeneInteractionAttribute dggia1 on dggi1.ggIntID=dggia1.ggIntID
								and dggia1.attrType='Provenance'and dggia1.attrValue=@dataSource							
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1
							join denorm.GeneGeneInteractionAttribute dggia2 on dggi2.ggIntID=dggia2.ggIntID
								and dggia2.attrType='Provenance' and dggia2.attrValue=@dataSource
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)		
end


-- NO taxid, molecule type or data source specified
if (@condition=8)
begin				
		Insert Into @tempgene (symbol,geneID, step)
		Select Distinct g.symbol, g.geneid, 0
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol 

		Insert Into @fltStartSet (symbol)
		Select Distinct g.symbol
		From @StartSet s 
			join geneR2.dbo.Gene g on s.symbol =g.symbol 
 
		-- Loop over steps to add neighboring genes	
		set @nstep = 1
		if (@filter='no')
		begin
			while(@nstep <= @steps)
			begin
				Insert Into @tempgene(symbol, geneID, step)
				Select distinct dggi.symbol2, dggi.geneid2, @nstep
				From @tempgene tg
					join denorm.GeneGeneInteraction dggi on tg.geneid =dggi.geneid1 
				Where dggi.symbol2 not in (select symbol from @tempgene)
				--Group by dggi.symbol2, dggi.geneid2	
				Set @nstep = @nstep + 1
			end
		end

		if (@filter='yes')
		begin
				while(@nstep <= @steps)
				begin					
					if (@nstep=2)
					begin						
						Insert into @tempgene(symbol, geneid, step)
						Select distinct dggi1.symbol2, dggi1.geneid2, @nstep
						From @tempgene tg
							join denorm.GeneGeneInteraction dggi1 on tg.geneid =dggi1.geneid1
								and tg.symbol in (select symbol from @fltStartSet) 
								and dggi1.symbol2 not in (select symbol from @tempgene)							
							join denorm.GeneGeneInteraction dggi2 on dggi1.geneid2=dggi2.geneid1							
								and dggi2.symbol2 in (select symbol from @fltStartSet)
								and tg.symbol !=dggi2.symbol2						
						--Group by g.symbol,g.geneid
					end					
					set @nstep = @nstep + 1					
				end
		end
		update @tempgene set step=0 where symbol in (select symbol from @StartSet)		
end
end

select * from @tempgene
END
GO
GRANT EXECUTE, VIEW DEFINITION ON mimiCytoPlugin.denormMimiR2InteractionMolecule
    TO db_executor;
GO



