-- =============================================
--	Jing Gao; David States
-- =============================================
USE mimiR2v1
IF OBJECT_ID ( 'mimiCytoPlugin.denormMimiR2PrecomputeExpand', 'P' ) IS NOT NULL 
    DROP PROCEDURE mimiCytoPlugin.denormMimiR2PrecomputeExpand
GO
CREATE procedure [mimiCytoPlugin].[denormMimiR2PrecomputeExpand]
	@geneid int,		
	@taxid int,
	@moleculeType varchar(50),
	@dataSource varchar(50)
as

BEGIN
	--specified taxid
	If (@moleculeType = 'All Molecule Types' and @dataSource='All Data Sources')
	Begin
		Select count(Distinct geneid2)
		From denorm.GeneGeneInteraction
		Where geneid1=@geneid and taxid2=@taxid and geneid1 != geneid2
	End

	--specified taxid,molecule type
	If (@moleculeType != 'All Molecule Types' and @dataSource='All Data Sources')
	Begin
		Select count(Distinct dggi.geneid2)
		From denorm.GeneGeneInteraction dggi
			join denorm.GeneAttribute dga on dggi.geneid2 = dga.geneid 
				and dga.attrType ='molecule type' and dga.attrValue=@moleculeType
		Where dggi.geneid1=@geneid and dggi.taxid2=@taxid and dggi.geneid1 != dggi.geneid2
	End

	--specified taxid,data source
	If (@moleculeType = 'All Molecule Types' and @dataSource !='All Data Sources')
	Begin
		Select count(Distinct dggi.geneid2)
		From denorm.GeneGeneInteraction dggi
			join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID =dggia.ggIntID
				and dggia.attrType = 'Provenance' and dggia.attrValue=@dataSource
		Where dggi.geneid1=@geneid and dggi.taxid2=@taxid and dggi.geneid1 != dggi.geneid2
	End

	--specified taxid,molecule type and data source
	If (@moleculeType != 'All Molecule Types' and @dataSource !='All Data Sources')
	Begin
		Select count(Distinct dggi.geneid2)
		From denorm.GeneGeneInteraction dggi
			join denorm.GeneAttribute dga on dggi.geneid2 = dga.geneid 
				and dga.attrType ='molecule type' and dga.attrValue=@moleculeType
			join denorm.GeneGeneInteractionAttribute dggia on dggi.ggIntID =dggia.ggIntID
				and dggia.attrType = 'Provenance' and dggia.attrValue=@dataSource
		Where dggi.geneid1=@geneid and dggi.taxid2=@taxid and dggi.geneid1 != dggi.geneid2
	End
END
GO
GRANT EXECUTE, VIEW DEFINITION ON mimiCytoPlugin.denormMimiR2PrecomputeExpand
    TO db_executor;
GO

