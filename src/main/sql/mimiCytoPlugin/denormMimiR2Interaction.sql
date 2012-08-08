
-- =============================================
--	Jing Gao, David States
-- =============================================
--get molecule interaction 
USE mimiR2v1
IF OBJECT_ID ( 'mimiCytoPlugin.denormMimiR2Interaction', 'P' ) IS NOT NULL 
    DROP PROCEDURE mimiCytoPlugin.denormMimiR2Interaction
GO
CREATE procedure [mimiCytoPlugin].[denormMimiR2Interaction]
	@list varchar(max),
	@steps int,
	@taxid int,	
	@moleculeType varchar(50),
	@dataSource varchar(50),
	@condition int,
	@filter varchar(10),
	@queryType int
as

Declare @tempgene table(
	symbol varchar(255),
	geneID int,    
	step int	
)


Declare @tempInterat table(
	symbol1 varchar(255),
	geneid1 int, 
	step1 int, 
	symbol2 varchar(255),
	geneid2 int, 
	step2 int, 
	intID int
)


insert into @tempgene 
exec mimiCytoPlugin.denormMimiR2InteractionMolecule @list, @steps, @taxid, @moleculeType,@dataSource,@condition,@filter,@queryType


If(@condition=2 or @condition=4 or @condition=6 or @condition=8 or @queryType=1)
Begin
	insert into @tempInterat 
	select g.symbol as symbol1, g.geneid as geneid1, g.step as step1,
		 g2.symbol as symbol2, g2.geneid as geneid2, g2.step as step2, dggi.ggIntID as intID	 
	from @tempgene g
		join denorm.GeneGeneInteraction dggi on g.geneid =dggi.geneid1
		join @tempgene g2 on g2.geneid =dggi.geneid2
	where g.symbol !=g2.symbol
	order by g.step+g2.step, g.symbol, g2.symbol	
End 
Else Begin
	insert into @tempInterat
	select g.symbol as symbol1, g.geneid as geneid1, g.step as step1,
		 g2.symbol as symbol2, g2.geneid as geneid2, g2.step as step2, dggi.ggIntID as intID	
	from @tempgene g
		join denorm.GeneGeneInteraction dggi on g.geneid =dggi.geneid1
		join @tempgene g2 on g2.geneid =dggi.geneid2
		join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID=dggia.ggIntID
	where g.symbol !=g2.symbol and attrType='Provenance' and attrValue=@dataSource
	order by g.step+g2.step, g.symbol, g2.symbol	
End

/* this is for non symmetric
insert into #tempInterat
select symbol2,geneid2,step2, symbol1,geneid1,step1, intID
from #tempInterat */



--denorm GeneGeneInteraction table is symmetric, 
	select distinct symbol1, geneid1, step1, symbol2,geneid2, step2, intID
	from @tempInterat
	where step1 < step2 or (step1=step2 and geneid1<geneid2) 
	group by symbol1, geneid1,step1, symbol2,geneid2, step2,intID
	order by step1,step2, symbol1, symbol2 
GO
GRANT EXECUTE, VIEW DEFINITION ON mimiCytoPlugin.denormMimiR2Interaction
    TO db_executor;
GO




